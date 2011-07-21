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

#include <isense/modules/cc_weather_module/ms55xx.h>
#include <isense/modules/core_module/core_module.h>

#include "external_interface/isense/isense_os.h"
#include "external_interface/isense/isense_radio.h"
#include "external_interface/isense/isense_timer.h"
#include "external_interface/isense/isense_debug.h"

#include "algorithms/neighbor_discovery/echo.h"
typedef wiselib::iSenseOsModel WiselibOs;
typedef wiselib::Echo<WiselibOs, WiselibOs::TxRadio, WiselibOs::Timer, WiselibOs::Debug> nb_t;

#include "../../isense_collector_app/src/collector_message.h"
typedef wiselib::CollectorMsg<WiselibOs, WiselibOs::TxRadio> collectorMsg_t;



#define TASK_SLEEP 1
#define TASK_WAKE 2
#define TASK_READ_SENSORS 2
#define TASK_SET_LIGHT_THRESHOLD 3
#define REPORT_INTERVAL 60
//----------------------------------------------------------------------------

using namespace isense;

class iSenseCollectorApplication :
public isense::Application,
public isense::Receiver,
public isense::Sender,
public isense::Task,
public isense::SleepHandler,
public IntegerDataHandler {
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

    virtual void handle_integer_data(int32 data) {

    };

    void ND_callback(uint8 event, uint16 from, uint8 len, uint8 * data) {
        if (data[0] == 1) {
            mygateway_ = from;
        }
    }

private:

    bool is_gateway() {
        switch (os().id()) {
            case 0x6699:        //2,3
                break;
            case 0x0498:        //2,1
                break;
            case 0x1b7f:        //3,3
                break;
            case 0x9979:        //0,1
                break;
            case 0xca7:         //0,2
                break;
            case 0x99ad:        //3,1
                break;
            default:
                return false;
        }
        return true;
    }

    bool gateway_;
    uint16 gateway_id;
    Ms55xx* ms_;

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

void iSenseCollectorApplication::boot(void) {
    ms_ = new Ms55xx(os());
    ms_->reset();
    
    isense::CoreModule * cm_ = new isense::CoreModule(os());
    cm_->led_on();

    os().dispatcher().add_receiver(this);
    os().allow_sleep(false);
    // register task to be called in a minute for periodic sensor readings
    os().add_task_in(Time(10, 0), this, (void*) TASK_READ_SENSORS);

    gateway_id = 0xffff;

    nb_.init(radio_, clock_, timer_, debug_, 2000, 15000, 200, 230);
    nb_.enable();

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

bool iSenseCollectorApplication::stand_by(void) {
    return true;
}
//----------------------------------------------------------------------------

bool iSenseCollectorApplication::hibernate(void) {
    return false;
}
//----------------------------------------------------------------------------

void iSenseCollectorApplication::wake_up(bool memory_held) {
}
//----------------------------------------------------------------------------

void iSenseCollectorApplication::execute(void* userdata) {



    if (!is_gateway()) {
        // read out sensor values, and debug
        int16 temp = ms_->get_temperature();
        temp = temp / 10;
        collectorMsg_t mess;
        mess.set_collector_type_id(collectorMsg_t::TEMPERATURE);
        mess.set_temperature(&temp);
//        os().debug("Contains temp %d - %d", mess.temperature(), temp);
        os().radio().send(mygateway_, mess.buffer_size(), (uint8*) & mess, 0, 0);

        uint16 bpressure = ms_->read_pressure();
        bpressure = bpressure / 10;
        collectorMsg_t mess1;
        mess1.set_collector_type_id(collectorMsg_t::BPRESSURE);
        mess1.set_bpressure(&bpressure);
//        os().debug("Contains bpressure %d - %d", mess1.bpressure(), bpressure);
        os().radio().send(mygateway_, mess1.buffer_size(), (uint8*) & mess1, 0, 0);
        //        debug(temp, lux);
    } else {
        int16 temp = ms_->get_temperature();
        int16 bpressure = ms_->read_pressure();
        os().debug("iSense::%x EM_T %d ", os().id(), temp / 10);
        os().debug("iSense::%x EM_P %d ", os().id(), bpressure / 10);
    }

    os().add_task_in(Time(REPORT_INTERVAL, 0), this, (void*) TASK_READ_SENSORS);
}
//----------------------------------------------------------------------------

void iSenseCollectorApplication::receive(uint8 len, const uint8 * buf, ISENSE_RADIO_ADDR_TYPE src_addr, ISENSE_RADIO_ADDR_TYPE dest_addr, uint16 signal_strength, uint16 signal_quality, uint8 seq_no, uint8 interface, Time rx_time) {

    if (dest_addr != os().id()) {
        //os().debug("listenig to mess fro %x by %x",dest_addr, src_addr);
    }
    if (!is_gateway()) return;

    collectorMsg_t * mess = (collectorMsg_t *) buf;

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
            os().debug("iSense::%x EM_E 1 ", src_addr);
        } else if (mess->collector_type_id() == collectorMsg_t::LINK_UP) {
            os().debug("iSense::%x LINK_UP %x ", mess->link_from(), mess->link_to());
        } else if (mess->collector_type_id() == collectorMsg_t::LINK_DOWN) {
            os().debug("iSense::%x LINK_DOWN %x ", mess->link_from(), mess->link_to());
        }
    }

}

//----------------------------------------------------------------------------

void iSenseCollectorApplication::confirm(uint8 state, uint8 tries, isense::Time time) {
};
//----------------------------------------------------------------------------

isense::Application * application_factory(isense::Os & os) {
    return new iSenseCollectorApplication(os);
}

