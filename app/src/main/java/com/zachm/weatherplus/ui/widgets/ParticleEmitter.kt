package com.zachm.weatherplus.ui.widgets

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Basic Particle Emitter in Compose
 * Zachary Martinson * 2025
 */

data class Particle(var x: Float, var y: Float, val size: Float, val gravity: Float)

val RainParticle = 0
val SnowParticle = 1

/**
 * @param modifier Modifier for the canvas
 * @param particleStartX Starting X position of the particles
 * @param particleStartY Starting Y position of the particles
 * @param particleSize Size of the particles
 */
@Composable
fun ParticleEmitter(modifier: Modifier, particleAmount: Int, particleStartX: Float, particleStartY: Float, particleSize: Float, particleRandomness: Float, particleType: Int) {

    val particles = remember { mutableStateListOf<Particle>() }

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    //Launch Effect on the lifecycle to create a coroutine, we do this to be lifecycle aware.
    LaunchedEffect(lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) { //This repeats when the app is in focus. Otherwise it stops the app.

            while(true) {
                repeat(particleAmount) {
                    when(particleType) {
                        RainParticle -> particles.add(Particle(particleStartX + (Random.nextFloat()*particleRandomness), particleStartY, particleSize, 70f + (Random.nextFloat()*5)))
                        SnowParticle -> particles.add(Particle(particleStartX + (Random.nextFloat()*particleRandomness), particleStartY, particleSize + (Random.nextFloat()*2), 10f + (Random.nextFloat()*5)))
                    }
                }
                //Removes particles with o(particleAmount) complexity. Stops frame jittering and speed ups.
                if(particles.size > 500) {
                    repeat(particleAmount) {
                        particles.removeFirstOrNull()
                    }
                }
                delay(16) //60FPS
            }
        }
    }

    Canvas(modifier = modifier) {
        particles.forEach {

            when(particleType) {
                RainParticle -> { //Draws a cylinder line with transparency at the end and high rate of gravity. Lower frequency if its lagging.

                    drawLine(
                        brush = Brush.linearGradient(
                            colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.2f) , Color.Transparent),
                            start = Offset(it.x,it.y),
                            end = Offset(it.x, it.y+140f)
                        ),
                        start = Offset(it.x, it.y),
                        end = Offset(it.x, it.y+140),
                        strokeWidth = it.size,
                        cap = StrokeCap.Round
                    )
                }
                SnowParticle -> { //Draws a circle at the cordinates.
                    drawCircle(
                        color = Color.White.copy(alpha = 0.2f),
                        center = Offset(it.x, it.y),
                        radius = it.size
                    )
                }
            }

            it.y += it.gravity
        }
    }

}