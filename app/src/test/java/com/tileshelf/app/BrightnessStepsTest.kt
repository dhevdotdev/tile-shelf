package com.tileshelf.app

import com.tileshelf.app.tiles.brightnessSteps
import com.tileshelf.app.tiles.nextBrightness
import org.junit.Assert.assertEquals
import org.junit.Test

class BrightnessStepsTest {
    @Test
    fun steps_scale_to_the_device_maximum() {
        assertEquals(listOf(0, 85, 170, 255), brightnessSteps(255))
        assertEquals(listOf(0, 1365, 2730, 4095), brightnessSteps(4095))
    }

    @Test
    fun a_level_between_steps_moves_up_to_the_next_step() {
        assertEquals(2730, nextBrightness(1485, brightnessSteps(4095)))
    }

    @Test
    fun the_top_step_wraps_to_the_bottom() {
        assertEquals(0, nextBrightness(4095, brightnessSteps(4095)))
    }

    @Test
    fun a_level_above_an_assumed_255_maximum_does_not_jump_to_zero_first() {
        assertEquals(1365, nextBrightness(1000, brightnessSteps(4095)))
    }
}
