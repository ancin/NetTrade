INSERT INTO tbl_sys_data_dict (id, version, PRIMARY_KEY, PRIMARY_VALUE, disabled, order_rank, parent_id) VALUES ('402880c53e10e5a0013e10e6dd250009', 1, 'PRIVILEGE_TYPE', '权限类型',0, 100, null);
INSERT INTO tbl_sys_data_dict (id, version, PRIMARY_KEY, PRIMARY_VALUE, disabled, order_rank, parent_id) VALUES ('402880c53e10e5a0013e10e6dd251000', 1, 'MENU', '菜单', 0, 500, '402880c53e10e5a0013e10e6dd250009');
INSERT INTO tbl_sys_data_dict (id, version, PRIMARY_KEY, PRIMARY_VALUE, disabled, order_rank, parent_id) VALUES ('402880c53e10e5a0013e10e6dd251001', 1, 'URL', 'URL', 0, 400, '402880c53e10e5a0013e10e6dd250009');
INSERT INTO tbl_sys_data_dict (id, version, PRIMARY_KEY, PRIMARY_VALUE, disabled, order_rank, parent_id) VALUES ('402880c53e10e5a0013e10e6dd251002', 1, 'BTN', '按钮', 0, 300, '402880c53e10e5a0013e10e6dd250009');

INSERT INTO tbl_sys_data_dict (id, version, PRIMARY_KEY, PRIMARY_VALUE, disabled, order_rank, parent_id) VALUES ('402880c53e10e5a0013e10e6dd251003', 0, 'BIZ_SALE_DELIVERY_ORDER_SOURCE', '销售单来源', 0, 100, null);
INSERT INTO tbl_sys_data_dict (id, version, PRIMARY_KEY, PRIMARY_VALUE, disabled, order_rank, parent_id) VALUES ('402880c53e10e5a0013e10e6dd251004', 0, 'JD', '京东', 0, 500, '402880c53e10e5a0013e10e6dd251003');
INSERT INTO tbl_sys_data_dict (id, version, PRIMARY_KEY, PRIMARY_VALUE, disabled, order_rank, parent_id) VALUES ('402880c53e10e5a0013e10e6dd251005', 0, 'TB', '淘宝', 0, 400, '402880c53e10e5a0013e10e6dd251003');
INSERT INTO tbl_sys_data_dict (id, version, PRIMARY_KEY, PRIMARY_VALUE, disabled, order_rank, parent_id) VALUES ('402880c53e10e5a0013e10e6dd251006', 0, 'TM', '天猫', 0, 300, '402880c53e10e5a0013e10e6dd251003');