package com.app.springbootquartzschedulerapplication.model;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TriggerInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private int triggerCount;

    private boolean isRunForever;

    private long timeInterval;

    private long initialOffset;

    private String info;
}
