package com.example.makeitso.model.service

import com.google.firebase.perf.trace
import com.google.firebase.perf.metrics.Trace

/**
 * Membuat jejak kinerja Firebase untuk suatu blok kode.
 *
 * Mendukung baik metode suspend maupun reguler.
 *
 * @param name Nama jejak yang akan dibuat
 * @param block Blok kode yang akan dilacak
 * @return Hasil dari blok kode yang dilacak
 */
inline fun <T> trace(name: String, block: Trace.() -> T): T = Trace.create(name).trace(block)
