#include "external_interface/external_interface.h"
#include "util/delegates/delegate.hpp"
#include "util/pstl/map_static_vector.h"
#include "util/pstl/static_string.h"

//ISENSE SENSORS
#include <isense/modules/core_module/core_module.h>
#include <isense/modules/environment_module/environment_module.h>
#include <isense/modules/environment_module/temp_sensor.h>
#include <isense/modules/environment_module/light_sensor.h>
#include <isense/modules/security_module/pir_sensor.h>
#include <isense/modules/security_module/lis_accelerometer.h>


typedef wiselib::OSMODEL Os;

//ND
#include "algorithms/neighbor_discovery/echo.h"
typedef wiselib::Echo<Os, Os::TxRadio, Os::Timer, Os::Debug> nb_t;

//MESSAGE_TYPES
#include "./collector_message.h"
typedef wiselib::CollectorMsg<Os, Os::TxRadio> collectorMsg_t;
typedef wiselib::BroadcastMsg<Os, Os::TxRadio> broadcastMsg_t;

//TYPEDEFS
typedef Os::TxRadio::node_id_t node_id_t;
typedef Os::TxRadio::block_data_t block_data_t;

//EVENT IDS
#define TASK_SLEEP 1
#define TASK_WAKE 2
#define TASK_READ_SENSORS 2
#define TASK_SET_LIGHT_THRESHOLD 3
#define TASK_BROADCAST_GATEWAY 4
#define TASK_TEST 5

#define REPORTING_INTERVAL 180

class Application
:
public isense::SensorHandler,
public isense::BufferDataHandler,
public isense::Int8DataHandler,
public isense::Uint32DataHandler {
public:

    //--------------------------------------------------------------

    /**
     * unused in this context
     * @param value
     */
    void handle_uint32_data(uint32 value) {
        //nothing
    }

    //--------------------------------------------------------------

    /**
     * unused in this context
     * @param value
     */
    void handle_int8_data(int8 value) {
        //nothing
    }

    /**
     * boot function
     * @param value pointer to os
     */
    void init(Os::AppMainParameter& value) {
        radio_ = &wiselib::FacetProvider<Os, Os::TxRadio>::get_facet(value);
        timer_ = &wiselib::FacetProvider<Os, Os::Timer>::get_facet(value);
        debug_ = &wiselib::FacetProvider<Os, Os::Debug>::get_facet(value);
        debug_->debug("*Boot*");
        uart_ = &wiselib::FacetProvider<Os, Os::Uart>::get_facet(value);
        clock_ = &wiselib::FacetProvider<Os, Os::Clock>::get_facet(value);

        cm_ = new isense::CoreModule(value);

        mygateway_ = 0xffff;


        em_ = new isense::EnvironmentModule(value);
        if (em_ != NULL) {
            em_->enable(true);
            if (em_->light_sensor()->enable()) {
                em_->light_sensor()->set_data_handler(this);
                //os().add_task_in(Time(10, 0), this, (void*) TASK_SET_LIGHT_THRESHOLD);
                debug_->debug("em light");
            }
            if (em_->temp_sensor()->enable()) {
                em_->temp_sensor()->set_data_handler(this);
                debug_->debug("em temp");
            }
        }


        pir_ = new isense::PirSensor(value);
        pir_->set_sensor_handler(this);
        pir_->set_pir_sensor_int_interval(2000);
        if (pir_->enable()) {
            pir_sensor_ = true;
            debug_->debug("id::%x em pir", radio_->id());
        }

        //        accelerometer_ = new isense::LisAccelerometer(value);
        //        if (accelerometer_ != NULL) {
        //            accelerometer_->set_mode(MODE_THRESHOLD);
        //            accelerometer_->set_threshold(25);
        //            accelerometer_->set_handler(this);
        //            accelerometer_->enable();
        //        }


        radio_->reg_recv_callback<Application, &Application::receive > (this);
        radio_->set_channel(12);

        uart_->reg_read_callback<Application, &Application::handle_uart_msg > (this);
        uart_->enable_serial_comm();

        debug_->debug("INIT ");

        nb_.init(*radio_, *clock_, *timer_, *debug_, 2000, 16000, 250, 255);
        nb_.enable();
        nb_. reg_event_callback<Application, &Application::ND_callback > ((uint8) 2, nb_t::NEW_NB | nb_t::NEW_NB_BIDI | nb_t::LOST_NB_BIDI | nb_t::DROPPED_NB, this);


        timer_->set_timer<Application, &Application::execute > (10000, this, (void*) TASK_READ_SENSORS);
        if (is_gateway()) {
            // register task to be called in a minute for periodic sensor readings
            timer_->set_timer<Application, &Application::execute > (1000, this, (void*) TASK_BROADCAST_GATEWAY);
            timer_->set_timer<Application, &Application::execute > (5000, this, (void*) TASK_TEST);
        }
    }
    // --------------------------------------------------------------------

    /**
     * Executed periodically
     * @param userdata TASK to perform
     */
    void execute(void* userdata) {

        // Get the Temperature and Luminance from sensors and debug them
        if ((long) userdata == TASK_READ_SENSORS) {
            if (radio_->id() != 0xddba) {
                timer_->set_timer<Application, &Application::execute > (REPORTING_INTERVAL * 1000, this, (void*) TASK_READ_SENSORS);
            }

            if (!is_gateway()) {

                int16 temp = em_->temp_sensor()->temperature();
                if (temp < 100) {
                    collectorMsg_t mess;
                    mess.set_collector_type_id(collectorMsg_t::TEMPERATURE);
                    mess.set_temperature(&temp);
                    debug_->debug("Contains temp %d -> %x ", temp, mygateway_);
                    radio_->send(mygateway_, mess.buffer_size(), (uint8*) & mess);
                }
                uint32 lux = em_->light_sensor()->luminance();
                if (lux < 20000) {
                    collectorMsg_t mess1;
                    mess1.set_collector_type_id(collectorMsg_t::LIGHT);
                    mess1.set_light(&lux);
                    debug_->debug("Contains light %d -> %x", lux, mygateway_);
                    radio_->send(mygateway_, mess1.buffer_size(), (uint8*) & mess1);
                }

            } else {
                int16 temp = em_->temp_sensor()->temperature();
                if (temp < 100) {
                    debug_->debug("id::%x EM_T %d ", radio_->id(), temp);

                }
                uint32 lux = em_->light_sensor()->luminance();
                if (lux < 20000) {
                    debug_->debug("id::%x EM_L %d ", radio_->id(), lux);
                }


            }
        } else if ((long) userdata == TASK_BROADCAST_GATEWAY) {
            debug_->debug("gateway");
            broadcastMsg_t msg;
            radio_->send(0xffff, msg.length(), (block_data_t*) & msg);
            timer_->set_timer<Application, &Application::execute > (20 * 1000, this, (void*) TASK_BROADCAST_GATEWAY);
        } else if ((long) userdata == TASK_TEST) {
            //handle_sensor();
            //timer_->set_timer<Application, &Application::execute > (3000, this, (void*) TASK_TEST);

        }
    }

protected:

    /**
     * Handles a new sensor reading
     */
    virtual void handle_sensor() {
        debug_->debug("pir event");
        if (!is_gateway()) {
            collectorMsg_t mess1;
            mess1.set_collector_type_id(collectorMsg_t::PIR);
            uint8_t pir = 1;
            mess1.set_pir_event(&pir);
            radio_->send(mygateway_, mess1.buffer_size(), (uint8*) & mess1);
        } else {
            isense::Time event_time = clock_->time();
            //            debug_->debug("id::%x EM_E 1 %d ", radio_->id(), event_time.sec_ * 1000 + event_time.ms_);
            debug_->debug("id::%x EM_E 1 ", radio_->id());
        }
    }

    /**
     * Handles a new accelerometer event
     * @param acceleration in 3 axis
     */
    virtual void handle_buffer_data(isense::BufferData* data) {
        if (!is_gateway()) {
            collectorMsg_t mess1;
            mess1.set_collector_type_id(collectorMsg_t::ACCELEROMETER);
            //            radio_->send(mygateway_, mess1.buffer_size(), (uint8*) & mess1);
        } else {
            debug_->debug("id::%x EM_A 1 ", radio_->id());
        }

        //return from continuous mode to threshold mode
        accelerometer_->set_mode(MODE_THRESHOLD);
    }

    /**
     * Handles a new neighborhood event
     * @param event event type
     * @param from neighbor id
     * @param len unused
     * @param data unused
     */
    void ND_callback(uint8 event, uint16 from, uint8 len, uint8 * data) {
        if (event == nb_t::NEW_NB_BIDI) {
            if (!is_gateway()) {
                uint16 id1, id2;
                id1 = radio_->id();
                id2 = from;
                collectorMsg_t mess;
                mess.set_collector_type_id(collectorMsg_t::LINK_UP);
                mess.set_link(id1, id2);

                radio_->send(mygateway_, mess.buffer_size(), (uint8*) & mess);
            } else {
                debug_->debug("id::%x LINK_UP %x ", radio_->id(), from);

            }
        } else if ((event == nb_t::LOST_NB_BIDI) || (event == nb_t::DROPPED_NB)) {
            if (!is_gateway()) {
                uint16 id1, id2;
                id1 = radio_->id();
                id2 = from;
                collectorMsg_t mess;
                mess.set_collector_type_id(collectorMsg_t::LINK_DOWN);
                mess.set_link(id1, id2);

                radio_->send(mygateway_, mess.buffer_size(), (uint8*) & mess);
            } else {
                debug_->debug("id::%x LINK_DOWN %x ", radio_->id(), from);
            }
        }
    }

    /**
     * Handle a new uart event
     * @param len payload length
     * @param mess payload buffer
     */
    void handle_uart_msg(Os::Uart::size_t len, Os::Uart::block_data_t *mess) {
        if (mess[0] == 9) {
            cm_->led_on();
        } else if (mess[0] == 10) {
            cm_->led_off();
        } else {

            node_id_t node;
            memcpy(&node, mess, sizeof (node_id_t));
            radio_->send(node, len - 2, (uint8*) mess + 2);
            debug_command(mess + 2, len - 2, node);
            if (len > 8) {
                char buffer[100];
                int bytes_written = 0;
                for (int i = 8; i < len; i++) {
                    bytes_written += sprintf(buffer + bytes_written, "%d", mess[i]);
                }
                buffer[bytes_written] = '\0';
                debug_->debug("FORWARDING to %x %s", node, buffer);
            }
        }



    }

    /**
     * Handle a new incoming message
     * @param src_addr
     * @param len
     * @param buf
     */
    void receive(node_id_t src_addr, Os::TxRadio::size_t len, block_data_t * buf) {

        if (!is_gateway()) {
            if (check_gateway(src_addr, len, buf)) return;
        } else {

            //USED for the XBEE inside offices
            //            bool doreturn = true;
            //            if ((radio_->id() == 0x585) && (src_addr == 0x42f)) doreturn = false;
            //            if (doreturn) {
            //                debug_->debug("doreturn");
            //                return;
            //            }

            if ((radio_->id() == 0x1ccd) && (src_addr == 0x42f)) {
                debug_->debug("case1");
                return;
            }

            if (check_air_quality(src_addr, len, buf)) {
                debug_->debug("check_air_quality");
                return;
            }

            check_collector(src_addr, len, buf);
        }
    }

    bool check_gateway(node_id_t src_addr, Os::TxRadio::size_t len, block_data_t * buf) {
        if ((len == 10) && (buf[0] == 0)) {
            bool sGmsg = true;
            for (int i = 1; i < 10; i++) {
                sGmsg = sGmsg && (buf[i] == i);
            }
            if (sGmsg) {
                mygateway_ = src_addr;
                debug_->debug("mygateway_->%x", mygateway_);
                return true;
            }
        }
        return false;
    }

    bool check_air_quality(node_id_t src_addr, Os::TxRadio::size_t len, block_data_t * buf) {
        if ((src_addr == 0x2c41) && (buf[0] == 0x43) && (0x1ccd == radio_->id())) {
            uint8 mess[len];
            memcpy(mess, buf, len);
            mess[len - 1] = '\0';

            if ((buf[1] == 0x4f) && (buf[2] == 0x32)) {
                debug_->debug("airquality::%x SVal1: %s ", src_addr, mess + 5);
            } else if (buf[1] == 0x4f) {
                debug_->debug("airquality::%x SVal2: %s ", src_addr, mess + 4);
            } else if (buf[1] == 0x48) {
                debug_->debug("airquality::%x SVal3: %s ", src_addr, mess + 5);
            }
            return true;
        }
        return false;
    }

    void check_collector(node_id_t src_addr, Os::TxRadio::size_t len, block_data_t * buf) {
        collectorMsg_t * mess;

        if ((buf[0] == 0x7f) || (buf[1] == 0x69) || (buf[2] == 112)) {

            uint8 msa[len - 3];
            memcpy(msa, buf + 3, len);
            if (len > 6) {
                msa[2] = buf[8];
                msa[3] = buf[7];
                msa[4] = buf[6];
                msa[5] = buf[5];
            }
            //                    swapped = ((num>>24)&0xff) | // move byte 3 to byte 0
            //                    ((num << 8)&0xff0000) | // move byte 1 to byte 2
            //                    ((num >> 8)&0xff00) | // move byte 2 to byte 1
            //                    ((num << 24)&0xff000000 // byte 0 to byte 3

            mess = (collectorMsg_t *) (msa);
            //        mess = (collectorMsg_t *) (buf + 3);
        } else {
            mess = (collectorMsg_t *) buf;
        }
        if (mess->msg_id() == collectorMsg_t::COLLECTOR_MSG_TYPE) {
            if (mess->collector_type_id() == collectorMsg_t::TEMPERATURE) {
                debug_->debug("id::%x EM_T %d ", src_addr, mess->get_int16());
            } else if (mess->collector_type_id() == collectorMsg_t::LIGHT) {
                debug_->debug("id::%x EM_L %d ", src_addr, mess->get_uint32());
            } else if (mess->collector_type_id() == collectorMsg_t::BPRESSURE) {
                debug_->debug("id::%x EM_P %d ", src_addr, mess->get_uint16());
            } else if (mess->collector_type_id() == collectorMsg_t::HUMIDITY) {
                debug_->debug("id::%x EM_H %d ", src_addr, mess->get_uint16());
            } else if (mess->collector_type_id() == collectorMsg_t::ACCELEROMETER) {
                debug_->debug("id::%x EM_A %d ", src_addr, mess->get_uint16());
            } else if (mess->collector_type_id() == collectorMsg_t::CHARGE) {
                debug_->debug("id::%x BA_C %d ", src_addr, mess->get_uint32());
            } else if (mess->collector_type_id() == collectorMsg_t::INFRARED) {
                debug_->debug("id::%x EM_I %d ", src_addr, mess->get_uint32());
            } else if (mess->collector_type_id() == collectorMsg_t::PIR) {
                debug_->debug("id::%x EM_E %d ", src_addr, mess->get_uint8());
                debug_payload(buf, len, src_addr);
            } else if (mess->collector_type_id() == collectorMsg_t::CO) {
                debug_->debug("id::%x SVal1: %d ", src_addr, mess->get_uint32());
            } else if (mess->collector_type_id() == collectorMsg_t::CH4) {
                debug_->debug("id::%x SVal3: %d ", src_addr, mess->get_uint32());
            } else if (mess->collector_type_id() == collectorMsg_t::LINK_UP) {
                debug_->debug("id::%x LINK_UP %x ", mess->link_from(), mess->link_to());
            } else if (mess->collector_type_id() == collectorMsg_t::LINK_DOWN) {
                debug_->debug("id::%x LINK_DOWN %x ", mess->link_from(), mess->link_to());
            } else if (mess->collector_type_id() == collectorMsg_t::ROOMLIGHTS) {
                debug_->debug("id::%x RL%d %d ", src_addr, mess->get_zone(), mess->get_status());
            } else if (mess->collector_type_id() == collectorMsg_t::CHAIR) {
                debug_->debug("id::%x CS %d ", src_addr, mess->get_uint8());
            } else {
                //debug_payload(buf, len, src_addr);
            }
        }
    }


private:

    bool is_gateway() {
        switch (radio_->id()) {
            case 0x6699: //2.3
            case 0x0498: //2.1
            case 0x1b7f: //3.3
            case 0x1ccd: //0.1
            case 0xc7a: //0.2
            case 0x99ad: //3,1
            case 0x8978: //1.1
                return true;
            default:
                return false;
        }
        return true;
    }

    void debug_payload(const uint8_t * payload, size_t length, node_id_t src) {
        char buffer[1024];
        int bytes_written = 0;
        bytes_written += sprintf(buffer + bytes_written, "pl(%x)(", src);
        for (size_t i = 0; i < length; i++) {
            bytes_written += sprintf(buffer + bytes_written, "%x|", payload[i]);
        }
        bytes_written += sprintf(buffer + bytes_written, ")");
        buffer[bytes_written] = '\0';
        debug_->debug("%s", buffer);
    }

    void debug_command(const uint8_t * payload, size_t length, node_id_t dest) {
        //TEMPLATE : "id::%x dest::%x command=1|2|3"
        char buffer[1024];
        int bytes_written = 0;
        bytes_written += sprintf(buffer + bytes_written, "id::%x dest::%x command=", radio_->id(), dest);
        for (size_t i = 0; i < length; i++) {
            bytes_written += sprintf(buffer + bytes_written, "%x|", payload[i]);
        }
        bytes_written += sprintf(buffer + bytes_written, " ");
        buffer[bytes_written] = '\0';
        debug_->debug("%s", buffer);
    }

    node_id_t mygateway_;
    nb_t nb_;
    bool pir_sensor_;

    isense::EnvironmentModule* em_;
    isense::LisAccelerometer* accelerometer_;
    isense::PirSensor* pir_;
    isense::CoreModule* cm_;

    Os::TxRadio::self_pointer_t radio_;
    Os::Timer::self_pointer_t timer_;
    Os::Debug::self_pointer_t debug_;
    Os::Uart::self_pointer_t uart_;
    Os::Clock::self_pointer_t clock_;

};

wiselib::WiselibApplication<Os, Application> application;
// --------------------------------------------------------------------------

void application_main(Os::AppMainParameter& value) {
    application.init(value);
}
