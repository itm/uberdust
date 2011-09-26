/************************************************************************
 ** This file is part of the the iSense project.
 ** Copyright (C) 2006 coalesenses GmbH (http://www.coalesenses.com)
 ** ALL RIGHTS RESERVED.
 ************************************************************************/
#include <isense/application.h>
#include <isense/os.h>
#include <isense/dispatcher.h>
#include <isense/radio.h>
#include <isense/hardware_radio.h>
#include <isense/task.h>
#include <isense/timeout_handler.h>
#include <isense/isense.h>
#include <isense/dispatcher.h>
#include <isense/time.h>
#include <isense/button_handler.h>
#include <isense/sleep_handler.h>
#include <isense/util/util.h>

#include <isense/config.h>

#include <isense/util/util.h>
#include <isense/modules/core_module/core_module.h>
#include <isense/modules/environment_module/environment_module.h>
#include <isense/modules/environment_module/temp_sensor.h>
#include <isense/modules/environment_module/light_sensor.h>
#include <isense/modules/gateway_module/gateway_module.h>
#include <isense/modules/solar_module/solar_module.h>
#include <isense/modules/security_module/pir_sensor.h>

#include <isense/uart.h>

#include "external_interface/isense/isense_os.h"
#include "external_interface/isense/isense_radio.h"
#include "external_interface/isense/isense_timer.h"
#include "external_interface/isense/isense_debug.h"
typedef wiselib::iSenseOsModel WiselibOs;


#include "algorithms/neighbor_discovery/echo.h"

typedef wiselib::Echo<WiselibOs, WiselibOs::TxRadio, WiselibOs::Timer, WiselibOs::Debug> nb_t;




#include "collector_message.h"

typedef wiselib::CollectorMsg<WiselibOs, WiselibOs::TxRadio> collectorMsg_t;



#define TASK_SLEEP 1
#define TASK_WAKE 2
#define TASK_READ_SENSORS 2
#define TASK_SET_LIGHT_THRESHOLD 3


#define MILLISECONDS 1000
//----------------------------------------------------------------------------

using namespace isense;

class iSenseCollectorApplication :
public isense::Application,
public isense::Receiver,
public isense::Sender,
public isense::Task,
public isense::SleepHandler,
public Uint32DataHandler,
public Int8DataHandler,
public SensorHandler {
public:
    iSenseCollectorApplication(isense::Os& os);

    virtual ~iSenseCollectorApplication();

    ///From isense::Application
    virtual void boot(void);

    ///From isense::SleepHandler
    virtual bool stand_by(void); // Memory held

    ///From isense::SleepHandler
    virtual bool hibernate(void); // Memory not held

    ///From isense::SleepHandler
    virtual void wake_up(bool memory_held);

    ///From isense::Receiver
    virtual void receive(uint8 len, const uint8 * buf, ISENSE_RADIO_ADDR_TYPE src_addr, ISENSE_RADIO_ADDR_TYPE dest_addr, uint16 signal_strength, uint16 signal_quality, uint8 seq_no, uint8 interface, Time rx_time);

    ///From isense::Sender
    virtual void confirm(uint8 state, uint8 tries, isense::Time time);

    ///From isense::Task
    virtual void execute(void* userdata);
    //--------------------------------------------------------------

    void handle_uint32_data(uint32 value) {
    }

    //--------------------------------------------------------------

    void handle_int8_data(int8 value) {
    }

    void debug_payload(const uint8_t * payload, size_t length, ISENSE_RADIO_ADDR_TYPE src) {
        char buffer[1024];
        int bytes_written = 0;
        bytes_written += sprintf(buffer + bytes_written, "payload(from %x)[", src);
        for (size_t i = 0; i < length; i++) {
            bytes_written += sprintf(buffer + bytes_written, "%x|", payload[i]);
        }
        bytes_written += sprintf(buffer + bytes_written, "]");
        buffer[bytes_written] = '\0';
        os().debug("%s", buffer);
    }

    virtual void handle_sensor() {
        if (!is_gateway()) {
            if (pir_sensor_) {
                collectorMsg_t mess1;
                mess1.set_collector_type_id(collectorMsg_t::PIR);
                uint8_t pir = 1;
                mess1.set_pir_event(&pir);
                os().radio().send(mygateway_, mess1.buffer_size(), (uint8*) & mess1, 0, 0);
            }
        } else {
            os().debug("iSense::%x EM_E 1 ", os().id());
        }


    }

    void debug(int16& temp, uint32& lux) {
        if (!is_gateway()) {

            int bidis = nb_.bidi_nb_size();
            uint8 mes[1 + sizeof (int16) + sizeof (uint32) + sizeof (int) ];
            mes[0] = 101;
            memcpy(mes + 1, &temp, sizeof (int16));
            memcpy(mes + 1 + sizeof (int16), &lux, sizeof (uint32));
            memcpy(mes + 1 + sizeof (int16), &lux, sizeof (uint32));
            memcpy(mes + 1 + sizeof (int16) + sizeof (uint32), &bidis, sizeof (int));
            os().radio().send(mygateway_, 1 + sizeof (int16) + sizeof (uint32) + sizeof (int), mes, 0, 0);

        }
    }

    void ND_callback(uint8 event, uint16 from, uint8 len, uint8 * data) {
        if (event == nb_t::NEW_PAYLOAD_BIDI) {
            if (data[0] == 1) {
                mygateway_ = from;
            }
        } else if (event == nb_t::NEW_NB_BIDI) {
            if (!is_gateway()) {
                uint16 id1, id2;
                id1 = os().id();
                id2 = from;
                collectorMsg_t mess;
                mess.set_collector_type_id(collectorMsg_t::LINK_UP);
                mess.set_link(id1, id2);

                os().radio().send(mygateway_, mess.buffer_size(), (uint8*) & mess, 0, 0);
            } else {
                os().debug("iSense::%x LINK_UP %x ", os().id(), from);

            }
        } else if ((event == nb_t::LOST_NB_BIDI) || (event == nb_t::DROPPED_NB)) {
            if (!is_gateway()) {
                uint16 id1, id2;
                id1 = os().id();
                id2 = from;
                collectorMsg_t mess;
                mess.set_collector_type_id(collectorMsg_t::LINK_DOWN);
                mess.set_link(id1, id2);

                os().radio().send(mygateway_, mess.buffer_size(), (uint8*) & mess, 0, 0);
            } else {
                os().debug("iSense::%x LINK_DOWN %x ", os().id(), from);
            }
        }
    }

private:

    bool is_gateway() {
        switch (os().id()) {
            case 0x6699:
            case 0x0498:
            case 0x1b7f:
            case 0x9979:
            case 0xc7a:
            case 0x99ad: //3,1
                return true;
            default:
                return false;
        }
        return true;
    }

    bool gateway_;
    bool temp_sensor_, light_sensor_, pir_sensor_;
    uint16 gateway_id;
    isense::EnvironmentModule* em_;
    isense::GatewayModule* gw_;
    isense::PirSensor* pir_;
    SolarModule* sm_;
    CoreModule* cm_;
    uint16 duty_cycle_;

    WiselibOs::TxRadio radio_;
    WiselibOs::Debug debug_;
    WiselibOs::Clock clock_;
    WiselibOs::Timer timer_;


    int channel;
    nb_t nb_;
    uint16 mygateway_;
};

//----------------------------------------------------------------------------

iSenseCollectorApplication::
iSenseCollectorApplication(isense::Os& os)
: isense::Application(os),
gateway_(false),
temp_sensor_(false),
light_sensor_(false),
pir_sensor_(false),
sm_(NULL),
duty_cycle_(3),
radio_(os_),
debug_(os_),
clock_(os_),
timer_(os_),
mygateway_(0xffff) {
}

//----------------------------------------------------------------------------

iSenseCollectorApplication::
~iSenseCollectorApplication() {
}

//----------------------------------------------------------------------------

void
iSenseCollectorApplication::
boot(void) {
    cm_ = new isense::CoreModule(os());
    em_ = new isense::EnvironmentModule(os());
    cm_->led_on();


    // create SolarModule instance
    sm_ = new SolarModule(os());


    // if allocation of SolarModule was successful
    if (sm_ != NULL) {
        // read out the battery state
        BatteryState bs = sm_->battery_state();
        // estimate battery charge from the battery voltage
        uint32 charge = sm_->estimate_charge(bs.voltage);
        // set the estimated battery charge
        sm_->set_battery_charge(charge);
        // output voltage and estimated charge
        //os().debug("Set estimated charge of %d uAh, voltage is %d mV", charge, bs.voltage);
    }
    // if allocation of EnvironmentModule was successful
    if (em_ != NULL) {
        em_->enable(true);
        if (em_->light_sensor()->enable()) {
            light_sensor_ = true;
            em_->light_sensor()->set_data_handler(this);
            //os().add_task_in(Time(10, 0), this, (void*) TASK_SET_LIGHT_THRESHOLD);
        } else {
            os().debug("%x;CAS", os().id());
        }
        if (em_->temp_sensor()->enable()) {
            temp_sensor_ = true;
            em_->temp_sensor()->set_data_handler(this);
        }
    }

    pir_ = new isense::PirSensor(os());
    pir_->set_sensor_handler(this);
    pir_->set_pir_sensor_int_interval(30000);
    if (pir_->enable()) {
        os().debug("Pe");
        pir_sensor_ = true;
    } else {
        os().debug("CAP");
    }


    if (is_gateway()) {
        os().dispatcher().add_receiver(this);
    }
    os().allow_sleep(false);
    // register task to be called in a minute for periodic sensor readings
    os().add_task_in(Time(10, 0), this, (void*) TASK_READ_SENSORS);


    gateway_id = 0xffff;
    nb_.init(radio_, clock_, timer_, debug_, 2000, 15000, 200, 230);
    nb_.enable();

    nb_. reg_event_callback<iSenseCollectorApplication, &iSenseCollectorApplication::ND_callback > ((uint8) 2, nb_t::NEW_PAYLOAD | nb_t::NEW_NB_BIDI | nb_t::LOST_NB_BIDI | nb_t::DROPPED_NB, this);
    nb_.register_payload_space((uint8) 2);

    uint8 buf;
    if (is_gateway()) {
        buf = 1;
    } else {
        buf = 0;
    }
    nb_.set_payload((uint8) 2, &buf, 1);

    os().radio().hardware_radio().set_channel(12);
    cm_->led_off();
}

//----------------------------------------------------------------------------

bool
iSenseCollectorApplication::
stand_by(void) {
    return true;
}

//----------------------------------------------------------------------------

bool
iSenseCollectorApplication::
hibernate(void) {
    return false;
}

//----------------------------------------------------------------------------

void
iSenseCollectorApplication::
wake_up(bool memory_held) {
}

//----------------------------------------------------------------------------

void
iSenseCollectorApplication::
execute(void* userdata) {
    cm_->led_on();
    //	os().debug("Channel %d",channel);
    if (os().id() == 0xddba) {
        if ((uint32) userdata == TASK_WAKE) {
            os().add_task_in(60000, this, (void*) TASK_WAKE);
            BatteryState bs = sm_->control();

            uint32 capacity = bs.capacity;
            collectorMsg_t mess;
            mess.set_collector_type_id(collectorMsg_t::CHARGE);
            mess.set_charge(&capacity);
            os().radio().send(mygateway_, mess.buffer_size(), (uint8*) & mess, 0, 0);


            if (bs.charge < 50000) {
                duty_cycle_ = 1; // 0.1%
            } else
                if (bs.capacity < 1000000) //1 Ah or less
            {
                duty_cycle_ = 100;
            } else
                if (bs.capacity < 3000000) //3Ah or less
            {
                duty_cycle_ = 300; // 30%
            } else
                if (bs.capacity < 5000000) {
                duty_cycle_ = 500; // 50%
            } else {
                duty_cycle_ = 880; // 88%
            }
            // add task to allow sleeping again
            os().add_task_in(duty_cycle_ * 60, this, (void*) TASK_SLEEP);
        } else if ((uint32) userdata == TASK_SLEEP) {
            //            os().debug("off");
            // allow sleeping again
            os().allow_sleep(true);
        }

    } else {
        // register as a task to wake up again in 1 minutes
        os().add_task_in(Time(180, 0), this, (void*) TASK_READ_SENSORS);

        //if (channel==27){channel=11;}

    }

    // Get the Temperature and Luminance from sensors and debug them
    if ((uint32) userdata == TASK_READ_SENSORS) {

        if (!is_gateway()) {
            if (temp_sensor_) {
                // read out sensor values, and debug
                int16 temp = em_->temp_sensor()->temperature();
                collectorMsg_t mess;
                mess.set_collector_type_id(collectorMsg_t::TEMPERATURE);
                mess.set_temperature(&temp);
                //os().debug("Contains temp %d - %d", mess.temperature(), temp);
                os().radio().send(mygateway_, mess.buffer_size(), (uint8*) & mess, 0, 0);
            }
            if (light_sensor_) {
                uint32 lux = em_->light_sensor()->luminance();
                collectorMsg_t mess1;
                mess1.set_collector_type_id(collectorMsg_t::LIGHT);
                mess1.set_light(&lux);
                //os().debug("Contains light %d - %d", mess1.light(), lux);
                os().radio().send(mygateway_, mess1.buffer_size(), (uint8*) & mess1, 0, 0);
                //        debug(temp, lux);
            }
        } else {
            if (temp_sensor_) {
                int16 temp = em_->temp_sensor()->temperature();
                os().debug("iSense::%x EM_T %d ", os().id(), temp);
            }
            if (light_sensor_) {
                uint32 lux = em_->light_sensor()->luminance();
                os().debug("iSense::%x EM_L %d ", os().id(), lux);
            }
        }

    }
    cm_->led_off();

}

//----------------------------------------------------------------------------

void
iSenseCollectorApplication::
receive(uint8 len, const uint8 * buf, ISENSE_RADIO_ADDR_TYPE src_addr, ISENSE_RADIO_ADDR_TYPE dest_addr, uint16 signal_strength, uint16 signal_quality, uint8 seq_no, uint8 interface, Time rx_time) {
    cm_->led_on();
    if (!is_gateway()) return;

    if ((src_addr == 0x2c41) && (buf[0] == 0x43) && (0x9979 == os().id())) {
        uint8 mess[len];
        memcpy(mess, buf, len);
        mess[len - 1] = '\0';

        //              os().debug("airquality::%x",src_addr);
        if ((buf[1] == 0x4f) && (buf[2] == 0x32)) {
            //telos->led_on(0);
            os().debug("airquality::%x SVal1: %s ", src_addr, mess + 5);
            //telos->led_off(0);
        } else if (buf[1] == 0x4f) {
            //telos->led_on(1);
            os().debug("airquality::%x SVal2: %s ", src_addr, mess + 4);
            //telos->led_off(1);
        } else if (buf[1] == 0x48) {
            //telos->led_on(2);
            os().debug("airquality::%x SVal3: %s ", src_addr, mess + 5);
            //telos->led_off(2);
        }
    }

    collectorMsg_t * mess;

    if ((buf[0] == 0x7f) || (buf[1] == 0x69) || (buf[2] == 112)) {

        mess = (collectorMsg_t *) (buf + 3);

    } else {
        mess = (collectorMsg_t *) buf;
    }

    if (mess->msg_id() == collectorMsg_t::COLLECTOR_MSG_TYPE) {

        //os().debug("Received a collector message from %x with %d", src_addr, mess->collector_type_id());
        if (mess->collector_type_id() == collectorMsg_t::TEMPERATURE) {
            os().debug("iSense::%x EM_T %d ", src_addr, mess->temperature());
            //            os().debug("iSense::%x LQI %d BIDIS %d EM_T %d EM_L %d", mess->temperature());
        } else if (mess->collector_type_id() == collectorMsg_t::LIGHT) {
            os().debug("iSense::%x EM_L %d ", src_addr, mess->light());
        } else if (mess->collector_type_id() == collectorMsg_t::BPRESSURE) {
            os().debug("iSense::%x EM_P %d ", src_addr, mess->bpressure());
        } else if (mess->collector_type_id() == collectorMsg_t::HUMIDITY) {
            os().debug("iSense::%x EM_H %d ", src_addr, mess->humidity());
        } else if (mess->collector_type_id() == collectorMsg_t::ACCELEROMETER) {
            os().debug("iSense::%x EM_A %d ", src_addr, mess->acceleration());
        } else if (mess->collector_type_id() == collectorMsg_t::CHARGE) {
            os().debug("iSense::%x BA_C %d ", src_addr, mess->charge());
        } else if (mess->collector_type_id() == collectorMsg_t::INFRARED) {
            os().debug("iSense::%x EM_I %d ", src_addr, mess->infrared());
        } else if (mess->collector_type_id() == collectorMsg_t::PIR) {
            os().debug("iSense::%x EM_E %d ", src_addr, mess->pir_event());
            debug_payload(buf, mess->buffer_size()+3, src_addr);

        } else if (mess->collector_type_id() == collectorMsg_t::CO) {
            os().debug("iSense::%x SVal1: %d ", src_addr, mess->light());
        } else if (mess->collector_type_id() == collectorMsg_t::CH4) {
            os().debug("iSense::%x SVal3: %d ", src_addr, mess->light());
        } else if (mess->collector_type_id() == collectorMsg_t::LINK_UP) {
            os().debug("iSense::%x LINK_UP %x ", mess->link_from(), mess->link_to());
            //            os().debug("from %x", src_addr);
        } else if (mess->collector_type_id() == collectorMsg_t::LINK_DOWN) {
            os().debug("iSense::%x LINK_DOWN %x ", mess->link_from(), mess->link_to());
            //            os().debug("from %x", src_addr);
        } else {
            debug_payload(buf, len, src_addr);
        }
    }
    cm_->led_off();
}

//----------------------------------------------------------------------------

void
iSenseCollectorApplication::
confirm(uint8 state, uint8 tries, isense::Time time) {
    //    os().debug("iSense::%x::Confirm", os().id());
};
//----------------------------------------------------------------------------

/**
 */
isense::Application * application_factory(isense::Os & os) {
    return new iSenseCollectorApplication(os);
}


/*-----------------------------------------------------------------------
 * Source  $Source: $
 * Version $Revision: 1.24 $
 * Date    $Date: 2006/10/19 12:37:49 $
 *-----------------------------------------------------------------------
 * $Log$
 *-----------------------------------------------------------------------*/
