/* 
 * File:   collector_msg.h
 * Author: Amaxilatis
 */

#ifndef COLLECTOR_MSG_H
#define	COLLECTOR_MSG_H

namespace wiselib {

    template
    < typename OsModel_P, typename Radio_P>
    class CollectorMsg {
    public:
        typedef OsModel_P OsModel;
        typedef Radio_P Radio;

        typedef typename Radio::node_id_t node_id_t;
        typedef typename Radio::size_t size_t;
        typedef typename Radio::block_data_t block_data_t;
        typedef typename Radio::message_id_t message_id_t;
        // message ids

        enum {
            COLLECTOR_MSG_TYPE = 102,
            TEMPERATURE = 0,
            LIGHT = 1,
            INFRARED = 2,
            HUMIDITY = 3,
            CO = 4,
            CO2 = 5,
            CH4 = 6,
            PIR = 7,
            CHARGE = 8,
            ACCELEROMETER = 9,
            LINK_UP = 10,
            BPRESSURE = 11,
            LINK_DOWN = 12
        };

        enum data_positions {
            MSG_ID_POS = 0, // message id position inside the message [uint8]
            COLLECTOR_ID_POS = 1,
            PAYLOAD_POS = 2
        };

        // --------------------------------------------------------------------

        CollectorMsg() {
            set_msg_id(COLLECTOR_MSG_TYPE);
        };
        // --------------------------------------------------------------------

        ~CollectorMsg() {
        };

        // get the message id

        inline message_id_t msg_id() {            
            return read<OsModel, block_data_t, uint8_t > (buffer + MSG_ID_POS);
            
        };
        // --------------------------------------------------------------------

        // set the message id

        inline void set_msg_id(message_id_t id) {
            write<OsModel, block_data_t, uint8_t > (buffer + MSG_ID_POS, id);
        };

        // get the collector id

        inline message_id_t collector_type_id() {
            return read<OsModel, block_data_t, uint8_t > (buffer + COLLECTOR_ID_POS);
        };
        // --------------------------------------------------------------------

        // set the collector id

        inline void set_collector_type_id(message_id_t id) {
            write<OsModel, block_data_t, uint8_t > (buffer + COLLECTOR_ID_POS, id);
        };

        inline uint8_t * payload() {
            return buffer + PAYLOAD_POS;
        };

        inline int16 temperature() {
            return read<OsModel, block_data_t, int16 > (buffer + PAYLOAD_POS);
        };

        inline uint16 bpressure() {
            return read<OsModel, block_data_t, uint16 > (buffer + PAYLOAD_POS);
        };

        inline uint32 light() {
            return read<OsModel, block_data_t, uint32 > (buffer + PAYLOAD_POS);
        };

        inline uint32 infrared() {
            return read<OsModel, block_data_t, uint32 > (buffer + PAYLOAD_POS);
        };

        inline uint16 humidity() {
            return read<OsModel, block_data_t, uint16 > (buffer + PAYLOAD_POS);
        };

        inline uint32 charge() {
            return read<OsModel, block_data_t, uint32 > (buffer + PAYLOAD_POS);
        };

        inline uint16 acceleration() {
            return read<OsModel, block_data_t, uint16 > (buffer + PAYLOAD_POS);
        };

        inline uint16 link_from() {
            return read<OsModel, block_data_t, uint16 > (buffer + PAYLOAD_POS);
        };

        inline uint16 link_to() {
            return read<OsModel, block_data_t, uint16 > (buffer + PAYLOAD_POS + sizeof (uint16));
        };

        inline uint8_t buffer_size() {
            return PAYLOAD_POS + 1 + payload_size();
        };

        uint8_t payload_size() {

            if (buffer[COLLECTOR_ID_POS] == TEMPERATURE) {
                return sizeof (int16);
            } else if (buffer[COLLECTOR_ID_POS] == LIGHT) {
                return sizeof (uint32);
            } else if (buffer[COLLECTOR_ID_POS] == INFRARED) {
                return sizeof (uint32);
            } else if (buffer[COLLECTOR_ID_POS] == HUMIDITY) {
                return sizeof (uint16);
            } else if (buffer[COLLECTOR_ID_POS] == CO) {
                return sizeof (uint32);
            } else if (buffer[COLLECTOR_ID_POS] == CO2) {
                return sizeof (uint32);
            } else if (buffer[COLLECTOR_ID_POS] == CH4) {
                return sizeof (uint32);
            } else if (buffer[COLLECTOR_ID_POS] == PIR) {
                return 0;
            } else if (buffer[COLLECTOR_ID_POS] == CHARGE) {
                return sizeof (uint32);
            } else if (buffer[COLLECTOR_ID_POS] == ACCELEROMETER) {
                return sizeof (uint32);
            } else if (buffer[COLLECTOR_ID_POS] == LINK_UP) {
                return 2 * sizeof (uint16);
            } else if (buffer[COLLECTOR_ID_POS] == LINK_DOWN) {
                return 2 * sizeof (uint16);
            } else if (buffer[COLLECTOR_ID_POS] == BPRESSURE) {
                return sizeof (uint16);
            } else {
                return 0;
            }

        };

        void set_humidity(uint16 * buf) {
            memcpy(buffer + PAYLOAD_POS, buf, sizeof (uint16));
        }

        void set_temperature(int16 *buf) {
            memcpy(buffer + PAYLOAD_POS, buf, sizeof (int16));
        };

        void set_bpressure(uint16 *buf) {
            memcpy(buffer + PAYLOAD_POS, buf, sizeof (uint16));
        };

        void set_light(uint32 *buf) {
            memcpy(buffer + PAYLOAD_POS, buf, sizeof (uint32));
        };

        void set_infrared(uint32 *buf) {
            memcpy(buffer + PAYLOAD_POS, buf, sizeof (uint32));
        };

        void set_charge(uint32 *buf) {
            memcpy(buffer + PAYLOAD_POS, buf, sizeof (uint32));
        };

        void set_link(uint16 *from, uint16 *to) {
            memcpy(buffer + PAYLOAD_POS, from, sizeof (uint16));
            memcpy(buffer + PAYLOAD_POS + sizeof (uint16), to, sizeof (uint16));
        };

    private:
        block_data_t buffer[Radio::MAX_MESSAGE_LENGTH]; // buffer for the message data
    };

}

#endif	/* ECHOMSG_H */



