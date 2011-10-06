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
#include <isense/time.h>
#include <isense/timer.h>
#include <isense/button_handler.h>
#include <isense/sleep_handler.h>
#include <isense/util/util.h>
#include <isense/modules/telosb_module/telosb_module.h>

#include <isense/platforms/msp430/msp430_os.h>
#include <isense/platforms/msp430/msp430_macros.h>

#include <io.h>
#include <signal.h>

//----------------------------------------------------------------------------
/**
 */

using namespace isense;

class iSenseDemoApplication :
	public isense::Application,
	public isense::Receiver,
	public isense::Sender,
	public isense::Task,
	public isense::TimeoutHandler,
	public isense::ButtonHandler,
	public isense::UartPacketHandler
{
public:
	iSenseDemoApplication(isense::Os& os);

	~iSenseDemoApplication() ;

	///From isense::Application
	void boot (void) ;

	///From isense::ButtonHandler
	void button_down( uint8 button );

	///From isense::ButtonHandler
	void button_up(uint8 button);

	///From isense::Receiver
	void receive (uint8 len, const uint8 * buf, ISENSE_RADIO_ADDR_TYPE src_addr, ISENSE_RADIO_ADDR_TYPE dest_addr, uint16 signal_strength, uint16 signal_quality, uint8 seq_no, uint8 interface, Time time) ;

	///From isense::Sender
	void confirm (uint8 state, uint8 tries, isense::Time time) ;

	///From isense::Task
	void execute( void* userdata ) ;

	///From isense::TimeoutHandler
	void timeout( void* userdata ) ;

	///From isense::UartPacketHandler
	void handle_uart_packet( uint8 type, uint8* buf, uint8 length );

private:

	TelosbModule *telos;
	int counter;

	bool is_gateway(){
		if (os().id()==0xf042){
		return true;
		}
		else {
		return false;
		}
	}



};

//----------------------------------------------------------------------------
iSenseDemoApplication::
	iSenseDemoApplication(isense::Os& os)
	: isense::Application(os),
		telos(NULL)
	{
	}

//----------------------------------------------------------------------------
iSenseDemoApplication::
	~iSenseDemoApplication()

	{
	}

//----------------------------------------------------------------------------
void
	iSenseDemoApplication::
	handle_uart_packet(uint8 type, uint8 * buf, uint8 length)
{
	os().debug("uart");
}

//----------------------------------------------------------------------------
void
	iSenseDemoApplication::
	boot(void)
	{
        os().debug("App::boot ");
        os().debug("App::boot %x",os().id());
		TOGGLE_LED(LED_BLUE);
        os().allow_doze(false);
        os().allow_sleep(false);


        telos = new TelosbModule(os_);

        telos->init();
        telos->add_button_handler(this);

        os().add_task_in(Time(5,0), this, NULL);

        os().uart(1).set_packet_handler(11,this);

        os().dispatcher().add_receiver(this);

//	os().radio().hardware_radio().set_channel(12);
	counter=0;

 	}

//----------------------------------------------------------------------------

void
	iSenseDemoApplication::
	button_down( uint8 button )
	{
		telos->led_on(0);
	}

void
	iSenseDemoApplication::
	button_up( uint8 button )
	{
		telos->led_off(0);
	}

//----------------------------------------------------------------------------
void
	iSenseDemoApplication::
	execute( void* userdata )
	{
	counter++;
	telos->led_on(1);
		os().add_task_in(Time(60,0), this, NULL);
	int16 temp = telos->temperature();
	int8 humid = telos->humidity();
	int16 light = telos->light();
	int16 inflight = telos->infrared();
	//iSense::%x LQI %d BIDIS %d EM_T %d EM_L %d",
	os().debug("id::%x EM_T %d " ,os().id(), temp/10 );
	os().debug("id::%x EM_H %d " ,os().id(), humid);
	os().debug("id::%x EM_I %d " ,os().id(), inflight );
	os().debug("id::%x EM_L %d " ,os().id(), light );
	telos->led_off(1);

	}

//----------------------------------------------------------------------------
void
	iSenseDemoApplication::
	receive (uint8 len, const uint8* buf, ISENSE_RADIO_ADDR_TYPE src_addr, ISENSE_RADIO_ADDR_TYPE dest_addr, uint16 signal_strength, uint16 signal_quality, uint8 seq_no, uint8 interface, Time time)
	{
	if ((src_addr==0x2c41)&&(buf[0]==0x43)&&(0xf042==os().id()) ){
		uint8 mess[len];
		memcpy(mess,buf,len);
		mess[len-1]='\0';

//		os().debug("airquality::%x",src_addr);
		if ( (buf[1]==0x4f) && (buf[2]==0x32) ){
		telos->led_on(0);
		os ().debug("airquality::%x SVal1:%s ",src_addr,mess+5);
		telos->led_off(0);
		}
		else if (buf[1]==0x4f) {
		telos->led_on(1);
		os ().debug("airquality::%x SVal2:%s ",src_addr,mess+4);
		telos->led_off(1);
		}
		else if (buf[1]==0x48){
		telos->led_on(2);
		os ().debug("airquality::%x SVal3:%s ",src_addr,mess+5);
		telos->led_off(2);
		}
	}

/*	if (src_addr!=0xddba) return;
	os().debug("Got a message from %x %d %x|%x|%x|%x|%x|%x|%x|%x %d",src_addr,len,buf[0],buf[1],buf[2],buf[3],buf[4],buf[5],buf[6],buf[7], 6+1 + sizeof (int16) + sizeof (uint32) + sizeof (int) );

//	if (!is_gateway()) return;

	if ((len == (6+1 + sizeof (int16) + sizeof (uint32) + sizeof (int))) && (buf[3] == 101)) {
	        int16 temp;
	        uint32 lux;
	        int bidis;
	        memcpy(&temp, buf+3 + 1, sizeof (int16));
	        memcpy(&lux, buf+3 + 1 + sizeof (int16), sizeof (uint32));
	        memcpy(&bidis, buf+3 + 1 + sizeof (int16) + sizeof (uint32), sizeof (int));
	        os().debug("iSense::%x LQI %d BIDIS %d EM_T %d EM_L %d",
        	        src_addr,
                	signal_quality,
	                bidis,
	                temp,
	                lux);
    	}

*/
//		os().debug("receive msg from %x", src_addr);
	}

//----------------------------------------------------------------------------
void
	iSenseDemoApplication::
	confirm (uint8 state, uint8 tries, isense::Time time)
	{
	}

//----------------------------------------------------------------------------
void
	iSenseDemoApplication::
	timeout( void* userdata )
	{
	}

//----------------------------------------------------------------------------
/**
  */
isense::Application* application_factory(isense::Os& os)
{
	return new iSenseDemoApplication(os);
}


/*-----------------------------------------------------------------------
* Source  $Source: $
* Version $Revision: 1.24 $
* Date    $Date: 2006/10/19 12:37:49 $
*-----------------------------------------------------------------------
* $Log$
*-----------------------------------------------------------------------*/
