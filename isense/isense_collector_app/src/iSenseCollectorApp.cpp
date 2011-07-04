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
#include <isense/modules/core_module/core_module.h>
#include <isense/modules/environment_module/environment_module.h>
#include <isense/modules/environment_module/temp_sensor.h>
#include <isense/modules/environment_module/light_sensor.h>
#include <isense/modules/security_module/pir_sensor.h>
#include <isense/modules/security_module/lis_accelerometer.h>

#include <isense/uart.h>

#include "external_interface/isense/isense_os.h"
#include "external_interface/isense/isense_radio.h"
#include "external_interface/isense/isense_timer.h"
#include "external_interface/isense/isense_debug.h"
typedef wiselib::iSenseOsModel WiselibOs;


#include "algorithms/neighbor_discovery/echo.h"


typedef wiselib::Echo<WiselibOs, WiselibOs::TxRadio, WiselibOs::Timer, WiselibOs::Debug> nb_t;


#define TASK_SET_LIGHT_THRESHOLD 1
#define TASK_READ_SENSORS 2
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
public SensorHandler,
public BufferDataHandler,
public Int8DataHandler,
public Uint32DataHandler {
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

    virtual void handle_sensor() {
        os().debug("iSense::%x::pir", os().id());
    };

    //-ACCELEROMETER EVENT------------------------------------------

    void handle_buffer_data(BufferData* buf_data) {
        os().debug("iSense::%x::ac", os().id());
        accelerometer_->set_mode(MODE_THRESHOLD);
    };
    //--------------------------------------------------------------

    void handle_uint32_data(uint32 value) {
    }

    //--------------------------------------------------------------

    void handle_int8_data(int8 value) {
    }

    void debug(int16& temp, uint32& lux) {
        if (!is_gateway()) {


            /* uint8 mess[1];
             mess[0] = 99;
             os().debug("sending...");
             os().radio().send(0xffff, 1, mess, 0, 0);
             */
            int bidis = nb_.bidi_nb_size();
            uint8 mes[1 + sizeof (int16) + sizeof (uint32) + sizeof (int) ];
            mes[0] = 101;
            memcpy(mes + 1, &temp, sizeof (int16));
            memcpy(mes + 1 + sizeof (int16), &lux, sizeof (uint32));
            memcpy(mes + 1 + sizeof (int16), &lux, sizeof (uint32));
            memcpy(mes + 1 + sizeof (int16) + sizeof (uint32), &bidis, sizeof (int));
            os().radio().send(0xffff, 1 + sizeof (int16) + sizeof (uint32) + sizeof (int), mes, 0, 0);

            /*          mess[0] = 100;
                      os().debug("sending...");
                      os().radio().send(0xffff, 1, mess, 0, 0);
             */
        }
/*        os().debug("iSense::%x LQI %d BIDIS %d EM_T %d EM_L %d",
                os().id(),
                255,
                nb_.bidi_nb_size(),
                temp,
                lux);*/
        //os().debug("is %d , %d %d",nb_.is_neighbor_bidi(0x1bbf),nb_.is_neighbor_bidi(0x14e6),nb_.is_neighbor_bidi(0x995a));

    }


private:

    bool is_gateway() {
        if (
                (os().id() == 0x6699) ||
                (os().id() == 0x0498) ||
                (os().id() == 0x1b7f) ||
                (os().id() == 0x9979) ||
                (os().id() == 0x99ad)
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
    // pointer to the accelerometer
    isense::LisAccelerometer* accelerometer_;
    // pointer to the passive infrared (PIR) sensor
    isense::PirSensor* pir_;

    WiselibOs::TxRadio radio_;
    WiselibOs::Debug debug_;
    WiselibOs::Clock clock_;
    WiselibOs::Timer timer_;

    int channel;
    nb_t nb_;


};

//----------------------------------------------------------------------------

iSenseCollectorApplication::
iSenseCollectorApplication(isense::Os& os)
: isense::Application(os),
counter_(true),
gateway_(false),
accelerometer_(NULL),
pir_(NULL),
radio_(os_),
debug_(os_),
clock_(os_),
timer_(os_) {
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
    //os().debug("Radio channel is  %d ",os_.radio().hardware_radio().channel());
    //os().debug("Radio channel is  %d ",os_.radio().hardware_radio().channel());


    // if allocation of EnvironmentModule was successful
    if (em_ != NULL) {
        if (em_->light_sensor() != NULL) {
            em_->light_sensor()->set_data_handler(this);
            os().add_task_in(Time(10, 0), this, (void*) TASK_SET_LIGHT_THRESHOLD);
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

    // create LisAccelerometer instance
    accelerometer_ = new LisAccelerometer(os());
    if (accelerometer_ != NULL) {
        accelerometer_->set_mode(MODE_THRESHOLD);
        accelerometer_->set_threshold(5);
        accelerometer_->set_handler(this);
        accelerometer_->enable();
        os().debug("iSense::%x::enabled ac", os().id());
    }

    // create PriSensor2 instance
    pir_ = new PirSensor(os());
    if (pir_ != NULL) {
        pir_->set_sensor_handler(this);
        pir_->set_pir_sensor_int_interval(2000);
        pir_->enable();
        os().debug("iSense::%x::enabled pir", os().id());

    }

    os_.uart(0).enable_interrupt(true);

    os_.uart(0).set_packet_handler(11, this);

    os().dispatcher().add_receiver(this);
    os().allow_sleep(false);
    // register task to be called in a minute for periodic sensor readings
    os().add_task_in(Time(10, 0), this, (void*) TASK_READ_SENSORS);


    counter_ = true;
    gateway_id = 0x6699;


    nb_.init(radio_, clock_, timer_, debug_, 2000, 15000, 200, 230);

    nb_.enable();
    os().radio().hardware_radio().set_channel(12);
    //nb_.register_debug_callback(0);

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
    	
    //	if (channel==27){channel=11;}
    // led blink
    if (counter_) {
        cm_->led_on();
        counter_ = false;
        //os().debug("Radio channel is  %d ",os_.radio().hardware_radio().channel());
    } else {
        cm_->led_off();
        counter_ = true;
    }

    // Get the Temperature and Luminance from sensors and debug them
    if ((uint32) userdata == TASK_READ_SENSORS) {



        // register as a task to wake up again in 2 minutes
        os().add_task_in(Time(60, 0), this, (void*) TASK_READ_SENSORS);

        // read out sensor values, and debug
        int16 temp = em_->temp_sensor()->temperature();
        uint32 lux = em_->light_sensor()->luminance();

        debug(temp, lux);
    }
    if ((uint32) userdata == TASK_SET_LIGHT_THRESHOLD) {
        // enable the sensor threshold mode, and configure it
        // to call the handler upon changes of 60% and more
        //em_->light_sensor()->enable_threshold_interrupt(true, 60);
    }

}

//----------------------------------------------------------------------------

void
iSenseCollectorApplication::
receive(uint8 len, const uint8 * buf, ISENSE_RADIO_ADDR_TYPE src_addr, ISENSE_RADIO_ADDR_TYPE dest_addr, uint16 signal_strength, uint16 signal_quality, uint8 seq_no, uint8 interface, Time rx_time) {
//    os().debug("Got a message form %x , len %d %d  , type %x|", src_addr, len, (1 + sizeof (int16) + sizeof (uint32) + sizeof (int)), buf[0]);
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
        os().debug("iSense::%x LQI %d BIDIS %d EM_T %d EM_L %d",
                src_addr,
                signal_quality,
                bidis,
                temp,
                lux);
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
