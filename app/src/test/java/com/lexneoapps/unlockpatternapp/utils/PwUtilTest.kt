package com.lexneoapps.unlockpatternapp.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PwUtilTest{


    @Test
    fun `transformation with given string returns correct list of int`(){
        val result = PwUtil.transformStringToIntList("123,456,789,")
        assertThat(result).isEqualTo(listOf(123,456,789))
    }

    @Test
    fun `different passwords including +- margin returns false`(){
        val result = PwUtil.checkIfSamePassword(listOf(16,20,50), listOf(10,20,50))
        assertThat(result).isFalse()
    }

    //tests for plus border
    @Test
    fun `correct passwords with first number on the + border including +- margin returns true`(){
        val result = PwUtil.checkIfSamePassword(listOf(15,20,50), listOf(10,20,50))
        assertThat(result).isTrue()
    }
    @Test
    fun `correct passwords with second number on the + border including +- margin returns true`(){
        val result = PwUtil.checkIfSamePassword(listOf(10,30,50), listOf(10,20,50))
        assertThat(result).isTrue()
    }

    @Test
    fun `correct passwords with third number on the + border including +- margin returns true`(){
        val result = PwUtil.checkIfSamePassword(listOf(10,20,75), listOf(10,20,50))
        assertThat(result).isTrue()
    }

    @Test
    fun `correct passwords with all numbers on the + border including +- margin returns true`(){
        val result = PwUtil.checkIfSamePassword(listOf(15,30,75), listOf(10,20,50))
        assertThat(result).isTrue()
    }

    //tests for minus border
    @Test
    fun `correct passwords with first number on the - border including +- margin returns true`(){
        val result = PwUtil.checkIfSamePassword(listOf(5,20,50), listOf(10,20,50))
        assertThat(result).isTrue()
    }
    @Test
    fun `correct passwords with second number on the - border including +- margin returns true`(){
        val result = PwUtil.checkIfSamePassword(listOf(10,10,50), listOf(10,20,50))
        assertThat(result).isTrue()
    }

    @Test
    fun `correct passwords with third number on the - border including +- margin returns true`(){
        val result = PwUtil.checkIfSamePassword(listOf(10,20,25), listOf(10,20,50))
        assertThat(result).isTrue()
    }

    @Test
    fun `correct passwords with all numbers on the - border including +- margin returns true`(){
        val result = PwUtil.checkIfSamePassword(listOf(5,10,25), listOf(10,20,50))
        assertThat(result).isTrue()
    }
}