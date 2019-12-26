package com.larenzhang.connectors.sqlserver;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @Description
 * @Author zhangguoli
 * @Date 9:57 2019/9/29
 **/
public interface SqlSerializableReturn<T> extends Serializable {
     PreparedStatement prepare(PreparedStatement pst,T o) throws SQLException;
}
    