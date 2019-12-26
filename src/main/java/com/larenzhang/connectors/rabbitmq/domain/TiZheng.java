package com.larenzhang.connectors.rabbitmq.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 体征信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TiZheng {
    public float tw;
    public String zzd;
    public String birth;
    public int sex;
    public float xl;
    public float xy;


}
