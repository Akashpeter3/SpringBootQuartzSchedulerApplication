package com.app.springbootquartzschedulerapplication.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TriggerInfo {

    private int triggerCount;

    private boolean isRunForever;

    private long timeInterval;

    private long initialOffset;
}
