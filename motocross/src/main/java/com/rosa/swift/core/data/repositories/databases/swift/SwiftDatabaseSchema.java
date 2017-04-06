package com.rosa.swift.core.data.repositories.databases.swift;

public class SwiftDatabaseSchema {

    public static final class Tables {

        public static final class Delivery {
            public static final String NAME = "delivery";

            public static final class Columns {
                public static final String NUMBER = "number";
                public static final String SET_NUMBER = "complement_number";
                public static final String COMMENTS = "comments";
                public static final String FIRST_PHONE = "first_phone";
                public static final String SECOND_PHONE = "second_phone";
                public static final String COST = "cost";
                public static final String CUSTOMER = "customer";
                public static final String LOGISTICIAN = "logistician";
                public static final String MANAGER = "manager";
                public static final String MANAGER_PHONE = "manager_phone";
                public static final String STATUS = "status";
                public static final String WEIGHT = "weight";
                public static final String VOLUME = "volume";
                public static final String PAY_ON_PLACE = "pay_on_place";
                public static final String LOADER_COST = "loader_cost";
                public static final String OVER_WEIGHT_COST = "over_weight_cost";
                public static final String ADDITION_COST = "addition_cost";
                public static final String ADDITION_COST_INFO = "addition_cost_info";
                public static final String PLANT_CODE = "plant_code";
                public static final String TRANSPORT_CODE = "transport_code";
                public static final String SET_ON_WAREHOUSE = "set_on_warehouse";
                public static final String SCHEMA_EXIST = "location_schema_exist";
                public static final String TYPE = "type";
                public static final String ADDRESS_FROM = "address_from";
                public static final String LONGITUDE_FROM = "longitude_from";
                public static final String LATITUDE_FROM = "latitude_from";
                public static final String ADDRESS_TO = "address_to";
                public static final String LONGITUDE_TO = "longitude_to";
                public static final String LATITUDE_TO = "latitude_to";
                public static final String START_DATE = "start_date";
                public static final String FINISH_DATE = "finish_date";
                public static final String STATUS_DATE = "status_date";
            }
        }

        public static final class DeliveryAddress {
            public static final String NAME = "delivery_address";

            public static final class Columns {
                public static final String DELIVERY_NUMBER = "delivery_number";
                public static final String ADDRESS = "address";
                public static final String LONGITUDE = "longitude";
                public static final String LATITUDE = "latitude";
                public static final String POSITION = "position";
                public static final String CONTACT_NAME = "contact_name";
                public static final String CONTACT_PHONE = "contact_phone";
            }
        }

        public static final class DeliveryWarehouse {
            public static final String NAME = "delivery_warehouse";

            public static final class Columns {
                public static final String DELIVERY_NUMBER = "delivery_number";
                public static final String CODE = "code";
                public static final String NAME = "name";
            }
        }

        public static final class DeliveryTemplate {
            public static final String NAME = "delivery_template";

            public static final class Columns {
                public static final String TEXT = "id";
                public static final String TYPE = "type";
                public static final String EVENING_TIME = "evening_time";
                public static final String LAST_UPDATED = "last_update";
            }
        }

        public static final class TransportLocation {
            public static final String NAME = "transport_location";

            public static final class Columns {
                public static final String TRANSPORTATION_ID = "id";
                public static final String TRANSPORTATION_STATUS = "status";
                public static final String LATITUDE = "latitude";
                public static final String LONGITUDE = "longitude";
                public static final String TIME = "time";
                public static final String TRANSFERRED = "transferred";
            }
        }

        public static final class Plant {
            public static final String NAME = "plant";

            public static final class Columns {
                public static final String CODE = "code";
                public static final String NAME = "name";
                public static final String ADDRESS = "address";
                public static final String LATITUDE = "latitude";
                public static final String LONGITUDE = "longitude";
                public static final String SELECTED = "selected";
                public static final String TPLST_CODE = "parent_code";
            }
        }

        public static final class Session {
            public static final String NAME = "session";

            public static final class Columns {
                public static final String SESSION_ID = "session_id";
                public static final String TYPE_TRANSPORT = "type_transport";
                public static final String LAST_QUEUE_ID = "last_queue_id";
                public static final String IS_LOGIN_ONLY = "is_login_only";
            }
        }

    }

}