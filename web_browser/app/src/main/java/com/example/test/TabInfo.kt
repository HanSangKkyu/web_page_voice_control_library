package com.example.test

import android.widget.Button

class TabInfo {
    lateinit var tag : String
    lateinit var blankFragment : BlankFragment
    lateinit var button : Button
    var url : String = ""

    constructor(tag : String, blankFragment : BlankFragment, button : Button) {
        this.tag = tag
        this.blankFragment = blankFragment
        this.button = button
    }



}