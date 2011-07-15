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
#include <isense/uart.h>
#include <isense/dispatcher.h>
#include <isense/time.h>
#include <isense/button_handler.h>
#include <isense/sleep_handler.h>
#include <isense/util/util.h>
#include <isense/modules/core_module/core_module.h>
#include <isense/config.h>

#include <isense/application.h>
#include <isense/os.h>
#include <isense/dispatcher.h>
#include <isense/radio.h>
#include <isense/task.h>
#include <isense/timeout_handler.h>
#include <isense/isense.h>
#include <isense/uart.h>
#include <isense/dispatcher.h>
#include <isense/time.h>
#include <isense/sleep_handler.h>
#include <isense/modules/pacemate_module/pacemate_module.h>
#include <isense/util/util.h>


#define ISENSE_ENABLE_WEATHER_MODULE 
/* Temperature and Relative Humidity Sensor */
#define ISENSE_ENABLE_SHTXX 
/* Barometric Pressure Sensor */
#define ISENSE_ENABLE_BAROMETER 

#include <isense/modules/core_module/core_module.h>
#include <isense/modules/environment_module/environment_module.h>
#include <isense/modules/environment_module/temp_sensor.h>
#include <isense/modules/environment_module/light_sensor.h>
#include <isense/modules/gateway_module/gateway_module.h>
#include <isense/modules/solar_module/solar_module.h>
#include <isense/modules/cc_weather_module/weather_module.h>

//#include <isense/modules/security_module/pir_sensor.h>
//#include <isense/modules/security_module/lis_accelerometer.h>

#include <isense/uart.h>

#include "external_interface/isense/isense_os.h"
#include "external_interface/isense/isense_radio.h"
#include "external_interface/isense/isense_timer.h"
#include "external_interface/isense/isense_debug.h"
typedef wiselib::iSenseOsModel WiselibOs;


#include "algorithms/neighbor_discovery/echo.h"

typedef wiselib::Echo<WiselibOs, WiselibOs::TxRadio, WiselibOs::Timer, WiselibOs::Debug> nb_t;





#define TASK_SLEEP 1
#define TASK_WAKE 2
#define TASK_READ_SENSORS 2
#define TASK_SET_LIGHT_THRESHOLD 3


#define MILLISECONDS 1000
//----------------------------------------------------------------------------
/**
 */




using namespace isense;

class iSenseCollectorApplication :
public isense::Application,
public isense::Receiver,
public isense::Sender,
public isense::Task,
public isense::SleepHandler,
public isense::UartPacketHandler,
//public SensorHandler,
//public BufferDataHandler,
public Uint32DataHandler
, public Int8DataHandler {
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

    //Send information through serial
    virtual void handle_uart_packet(uint8, uint8*, uint8);



    //-PIR SENSOR EVENT---------------------------------------------
    /*
        virtual void handle_sensor() {
            os().debug("iSense::%x::pir", os().id());
        };

        //-ACCELEROMETER EVENT------------------------------------------

        void handle_buffer_data(BufferData* buf_data) {
            os().debug("iSense::%x::ac", os().id());
            accelerometer_->set_mode(MODE_THRESHOLD);
        };
     */
    //--------------------------------------------------------------

    void handle_uint32_data(uint32 value) {
    }

    //--------------------------------------------------------------

    void handle_int8_data(int8 value) {        
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
            //os().debug("Sending data to %x",mygateway_);

        }
        /*        os().debug("iSense::%x LQI %d BIDIS %d EM_T %d EM_L %d",
                        os().id(),
                        255,
                        nb_.bidi_nb_size(),
                        temp,
                        lux);
         */
        //os().debug("is %d , %d %d",nb_.is_neighbor_bidi(0x1bbf),nb_.is_neighbor_bidi(0x14e6),nb_.is_neighbor_bidi(0x995a));
    }

    void ND_callback(uint8 event, uint16 from, uint8 len, uint8 * data) {
        if (data[0] == 1) {
            mygateway_ = from;
        }
        //os().debug("Node %x Got node %x as %x mygateway_%x", os().id(), from, data[0], mygateway_);
    }


private:

    bool is_gateway() {
        if (
                (os().id() == 0xca2) || //2.3
                (os().id() == 0x6699) || //2.3
                (os().id() == 0x0498) || //2.1
                (os().id() == 0x1b7f) || //3.3
                (os().id() == 0x9979) || //0.1
                (os().id() == 0x99ad) //3.1
                ) {
            return true;
        } else {
            return false;
        }
    }

    bool counter_;
    bool gateway_;
    uint16 gateway_id;
    isense::CoreModule* cm_;
    isense::EnvironmentModule* em_;
    isense::GatewayModule* gw_;
    SolarModule* sm_;    
    // duty cycle of the sensor node in tenth of a percent
    // e.g. duty_cycle == 150 --> 15%
    uint16 duty_cycle_;
    bool gway_;
    // pointer to the accelerometer
    //    isense::LisAccelerometer* accelerometer_;
    // pointer to the passive infrared (PIR) sensor
    //    isense::PirSensor* pir_;

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
counter_(true),
gateway_(false),
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
        os().debug("Set estimated charge of %d uAh, voltage is %d mV", charge, bs.voltage);
    }
    cm_->led_on();

    // if allocation of EnvironmentModule was successful
    if (em_ != NULL) {
        if (em_->light_sensor() != NULL) {
            em_->light_sensor()->set_data_handler(this);
            //os().add_task_in(Time(10, 0), this, (void*) TASK_SET_LIGHT_THRESHOLD);
        } else {
            os().debug("iSense::%x Could not allocate light sensor", os().id());
        }
        if (em_->temp_sensor() != NULL) {
            em_->temp_sensor()->set_data_handler(this);
        } else {
            os().debug("iSense::%x Could not allocate temp sensor", os().id());
        }

        os().debug("iSense::%x::enabled em", os().id());
        em_->enable(true);
    }

    os_.uart(0).enable_interrupt(true);

    os_.uart(0).set_packet_handler(11, this);

    os().dispatcher().add_receiver(this);
    os().allow_sleep(false);
    // register task to be called in a minute for periodic sensor readings
    os().add_task_in(Time(10, 0), this, (void*) TASK_READ_SENSORS);


    counter_ = true;
    gateway_id = 0xffff;


    nb_.init(radio_, clock_, timer_, debug_, 2000, 15000, 200, 230);
    nb_.enable();

    //uint8 flags = nb_t::NEW_PAYLOAD_BIDI ;

    nb_. reg_event_callback<iSenseCollectorApplication, &iSenseCollectorApplication::ND_callback > ((uint8) 2, nb_t::NEW_PAYLOAD, this);
    nb_.register_payload_space((uint8) 2);

    uint8 buf[1];
    if (is_gateway()) {
        buf[0] = 1;
    } else {
        buf[0] = 0;
    }
    nb_.set_payload((uint8) 2, buf, 1);

    os().radio().hardware_radio().set_channel(12);

}

//----------------------------------------------------------------------------

bool
iSenseCollectorApplication::
stand_by(void) {
    os().debug("iSense::%x::sleep", os().id());
    return true;
}

//----------------------------------------------------------------------------

bool
iSenseCollectorApplication::
hibernate(void) {
    os().debug("iSense::%x::hibernate", os().id());
    return false;
}

//----------------------------------------------------------------------------

void
iSenseCollectorApplication::
wake_up(bool memory_held) {
    os().debug("iSense::%x::Wakeup", os().id());
}

//----------------------------------------------------------------------------

void
iSenseCollectorApplication::
execute(void* userdata) {
    //	os().debug("Channel %d",channel);


    if (os().id() == 0xddba) {
        if ((uint32) userdata == TASK_WAKE) {
            os().add_task_in(60000, this, (void*) TASK_WAKE);
            BatteryState bs = sm_->control();
            os().debug("voltage=%dmV, charge=%iuAh -> duty cycle=%d, current=%i", bs.voltage, bs.capacity, duty_cycle_, bs.current);
            uint8 mess[10];
            uint16 voltage = bs.voltage;
            uint32 capacity = bs.capacity;
            uint32 current = bs.current;
            memcpy(mess, &voltage, 2);
            memcpy(mess + 2, &capacity, 4);
            memcpy(mess + 2 + 4, &current, 4);
            os().radio().send(0x9979, 10, mess, 0, 0);



            if (bs.charge < 50000) {
                // less than 50mAh -->
                // battery nearly empty -->
                // set ultra-low duty cycle
                // ~100uA current used by the node
                // live ~20 days
                duty_cycle_ = 1; // 0.1%
            } else
                if (bs.capacity < 1000000) //1 Ah or less
            {
                // 10% --> 4,5mA current used by node
                //live approx. 9 days out of 1Ah
                // and then another 20 days at 0.1% duty cycle
                // (see case above)
                duty_cycle_ = 100;
            } else
                if (bs.capacity < 3000000) //3Ah or less
            {
                // set duty cycle to 30% -->
                // 13,5mA current used by node -->
                // live approx. 6 days out of 1Ah
                // and then another 9 days at 10% duty cycle
                // and then another 20 days at 0.1% duty cycle
                // (see cases above)
                duty_cycle_ = 300; // 30%
            } else
                if (bs.capacity < 5000000) {
                // set duty cycle to 50% -->
                // 22,5mA current used by node -->
                // live approx. 4 days out of 2Ah
                // and then another 6 days at 30%
                // and then another 9 days at 10% duty cycle
                // and then another 20 days at 0.1% duty cycle
                // (see cases above)
                duty_cycle_ = 500; // 50%
            } else {
                // set duty cycle to 88% -->
                // (7 seconds per minute left sleeping to
                // measure charge current)
                // 39,6mA current used by node -->
                // live approx. 1.5 days out of 1.4Ah
                // and then another 4 days at 40%
                // and then another 6 days at 30%
                // and then another 9 days at 10% duty cycle
                // and then another 20 days at 0.1% duty cycle
                // (see cases above)
                duty_cycle_ = 880; // 88%
            }
            // add task to allow sleeping again
            os().add_task_in(duty_cycle_ * 60, this, (void*) TASK_SLEEP);


        } else if ((uint32) userdata == TASK_SLEEP) {
            os().debug("off");
            // allow sleeping again
            os().allow_sleep(true);
        }

    } else {
        // register as a task to wake up again in 1 minutes
        os().add_task_in(Time(60, 0), this, (void*) TASK_READ_SENSORS);

        //if (channel==27){channel=11;}
        // led blink
        if (counter_) {
            //cm_->led_on();
            counter_ = false;
            //os().debug("Radio channel is  %d ",os_.radio().hardware_radio().channel());
        } else {
            //cm_->led_off();
            counter_ = true;
        }

    }

    // Get the Temperature and Luminance from sensors and debug them
    if ((uint32) userdata == TASK_READ_SENSORS) {

        // read out sensor values, and debug
        int16 temp = em_->temp_sensor()->temperature();
        uint32 lux = em_->light_sensor()->luminance();

        debug(temp, lux);
    }

}

//----------------------------------------------------------------------------

void
iSenseCollectorApplication::
receive(uint8 len, const uint8 * buf, ISENSE_RADIO_ADDR_TYPE src_addr, ISENSE_RADIO_ADDR_TYPE dest_addr, uint16 signal_strength, uint16 signal_quality, uint8 seq_no, uint8 interface, Time rx_time) {

    //    if (src_addr == 0xddba) {
    //        os().debug("Got a message form %x , len %d %d  , type %x|", src_addr, len, (1 + sizeof (int16) + sizeof (uint32) + sizeof (int)), buf[0]);
    //    }


    if (dest_addr != os().id()) {
        //os().debug("listenig to mess fro %x by %x",dest_addr, src_addr);
    }
    if (!is_gateway()) return;

    if ((src_addr == 0x2c41) && (buf[0] == 0x43) && (0x9979 == os().id())) {
        uint8 mess[len];
        memcpy(mess, buf, len);
        mess[len - 1] = '\0';

        //              os().debug("airquality::%x",src_addr);
        if ((buf[1] == 0x4f) && (buf[2] == 0x32)) {
            //telos->led_on(0);
            os().debug("airquality::%x SVal1:%s ", src_addr, mess + 5);
            //telos->led_off(0);
        } else if (buf[1] == 0x4f) {
            //telos->led_on(1);
            os().debug("airquality::%x SVal2:%s ", src_addr, mess + 4);
            //telos->led_off(1);
        } else if (buf[1] == 0x48) {
            //telos->led_on(2);
            os().debug("airquality::%x SVal3:%s ", src_addr, mess + 5);
            //telos->led_off(2);
        }
    }



    if ((len == (1 + sizeof (int16) + sizeof (uint32) + sizeof (int))) && (buf[0] == 101)) {
        int16 temp;
        uint32 lux;
        int bidis;
        memcpy(&temp, buf + 1, sizeof (int16));
        memcpy(&lux, buf + 1 + sizeof (int16), sizeof (uint32));
        memcpy(&bidis, buf + 1 + sizeof (int16) + sizeof (uint32), sizeof (int));
        os().debug("iSense::%x Source%x LQI %d BIDIS %d EM_T %d EM_L %d",
                src_addr,
                os().id(),
                signal_quality,
                bidis,
                temp,
                lux);
    }

    if ((src_addr == 0xddba) && (len == 10)) {
        uint16 voltage;
        memcpy(&voltage, buf, 2);
        uint32 capacity;
        memcpy(&capacity, buf + 2, 4);
        uint32 current;
        memcpy(&current, buf + 2 + 4, 4);
        os().debug("id %x - voltage=%dmV, charge=%iuAh -> current=%i", src_addr, voltage, capacity, current);

        //os().debug(" %d - %i - %i", src_addr, voltage, capacity, current);
    }

}

//----------------------------------------------------------------------------

void
iSenseCollectorApplication::
confirm(uint8 state, uint8 tries, isense::Time time) {
    os().debug("iSense::%x::Confirm", os().id());
};
//----------------------------------------------------------------------------

void
iSenseCollectorApplication::
handle_uart_packet(uint8 type, uint8* buf, uint8 len) {
    //    os().debug("iSense::%x::Uart", os().id());
    //    for (int i = 0; i < len; i++) {
    //        os().debug("iSense::%x", buf[i]);
    //    }
}

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
