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
            LINK_UP = 0xa,
            BPRESSURE = 0xb,
            LINK_DOWN = 0xc,
            CHAIR = 0xd,
            ROOMLIGHTS = 0xe


        };

        enum {
            SITTING = 1,
            NOSITTING = 0,
            ZONE_1 = 0x1,
            ZONE_2 = 0x2,
            ZONE_3 = 0x3,
            ZONE_4 = 0x4
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
            return buffer[MSG_ID_POS];

        };
        // --------------------------------------------------------------------

        // set the message id

        inline void set_msg_id(message_id_t id) {
            write<OsModel, block_data_t, uint8_t > (buffer + MSG_ID_POS, id);
        };

        // get the collector id

        inline message_id_t collector_type_id() {
            return buffer[COLLECTOR_ID_POS];
        };
        // --------------------------------------------------------------------

        // set the collector id

        inline void set_collector_type_id(message_id_t id) {
            write<OsModel, block_data_t, uint8_t > (buffer + COLLECTOR_ID_POS, id);
        };

        inline uint8_t * payload() {
            return buffer + PAYLOAD_POS;
        };

        inline int16 get_int16() {
            return read<OsModel, block_data_t, int16 > (buffer + PAYLOAD_POS);
        };

        inline uint16 get_uint16() {
            return read<OsModel, block_data_t, uint16 > (buffer + PAYLOAD_POS);
        };

        inline uint32 get_uint32() {
            return read<OsModel, block_data_t, uint32 > (buffer + PAYLOAD_POS);
        };

        inline node_id_t link_from() {
            return read<OsModel, block_data_t, node_id_t > (buffer + PAYLOAD_POS);
        };

        inline node_id_t link_to() {
            return read<OsModel, block_data_t, node_id_t > (buffer + PAYLOAD_POS + sizeof (node_id_t));
        };

        inline uint8_t buffer_size() {
            return PAYLOAD_POS + payload_size();
        };

        inline uint8_t get_uint8() {
            return buffer[PAYLOAD_POS];
        }

        //actually the 1st byte but need the 4th to fix endianness

        inline uint8_t get_zone() {
            return buffer[PAYLOAD_POS + 3];
        }

        //actually the 2nd byte but need the 3rd to fix endianness

        inline uint8_t get_status() {
            return buffer[PAYLOAD_POS + 2];
        }

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
                return sizeof (uint8_t);
            } else if (buffer[COLLECTOR_ID_POS] == CHARGE) {
                return sizeof (uint32);
            } else if (buffer[COLLECTOR_ID_POS] == ACCELEROMETER) {
                return sizeof (uint32);
            } else if (buffer[COLLECTOR_ID_POS] == LINK_UP) {
                return 2 * sizeof (node_id_t);
            } else if (buffer[COLLECTOR_ID_POS] == LINK_DOWN) {
                return 2 * sizeof (node_id_t);
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

        void set_link(node_id_t from, node_id_t to) {
            write<OsModel, block_data_t, node_id_t > (buffer + PAYLOAD_POS, from);
            write<OsModel, block_data_t, node_id_t > (buffer + PAYLOAD_POS + sizeof (node_id_t), to);
        };

        void set_pir_event(uint8_t *buf) {
            memcpy(buffer + PAYLOAD_POS, buf, sizeof (uint8_t));
        }

    private:
        block_data_t buffer[Radio::MAX_MESSAGE_LENGTH]; // buffer for the message data
    };

}

#endif	/* ECHOMSG_H */



