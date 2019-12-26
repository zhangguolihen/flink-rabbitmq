package com.larenzhang.connectors.sqlserver;

import com.larenzhang.connectors.rabbitmq.domain.TiZheng;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @Description sql参数设置
 * @Author zhangguoli
 * @Date 10:01 2019/9/29
 **/
public  class SqlSerializableReturnImpl<T> implements SqlSerializableReturn<T> {
    private static final long serialVersionUID = 1L;
    @Override
    public PreparedStatement prepare(PreparedStatement pst, T o) throws SQLException {
        if(o instanceof TiZheng){
            TiZheng tiZheng=(TiZheng)o;
            pst.setFloat(1,tiZheng.tw);
            pst.setString(2,tiZheng.zzd);
            pst.setString(3,tiZheng.birth);
            pst.setInt(4,tiZheng.sex);
            pst.setFloat(5,tiZheng.xl);
            pst.setFloat(6,tiZheng.xy);
        }
        return pst;
    }
}
    