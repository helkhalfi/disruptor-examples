package com.mcba.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * Created by Hichame EL KHALFI on 01/06/2015.
 */
public class CarEventFactory implements EventFactory<Car> {

    public Car newInstance() {

        return new Car();
    }


}
