package com.wary;

import me.shedaniel.autoconfig.ConfigData;

@me.shedaniel.autoconfig.annotation.Config(name = "debughudmodifier")
public class Config implements ConfigData {
    public int offsetx = 0;
    public int offsetz = 0;
    public int spawnradius = 10000;
    public boolean disableinspawn = false;
    public boolean fakecoordsenabled = false;
    public boolean testoption = false;
}
