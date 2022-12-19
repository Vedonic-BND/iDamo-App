package com.android.vedonic.idamo.model

class Weather {

    private var time: String = ""
    private var temperature: String = ""
    private var icon: String = ""
    private var condition: String = ""

    constructor()

    constructor(time: String, temperature: String, icon: String, condition: String) {
        this.time = time
        this.temperature = temperature
        this.icon = icon
        this.condition = condition

    }

    fun getTime(): String {
        return time
    }

    fun setTime(time: String) {
        this.time = time
    }


    fun getTemperature(): String {
        return temperature
    }

    fun setTemperature(temperature: String) {
        this.temperature = temperature
    }


    fun getIcon(): String {
        return icon
    }

    fun setIcon(icon: String) {
        this.icon = icon
    }


    fun getCondition(): String {
        return condition
    }

    fun setCondition(condition: String) {
        this.condition = condition
    }
}