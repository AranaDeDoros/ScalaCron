package org.anaradedoros
package dsl

import domain.Models.*

object DSL :
  // -----------------------------------------------------------
  // DSL
  // -----------------------------------------------------------
  object CronDSL:

    val * = "*"

    extension (i: Int)
      def m: CronExpr[Int] = At(Minute.unsafe(i))
      def h: CronExpr[Int] = At(Hour.unsafe(i))
      def dom: CronExpr[Int] = At(DayOfMonth.unsafe(i))
      def dow: CronExpr[Int] = At(DayOfWeek.unsafe(i))

    extension (star: String)
      def /(step: Int)(using field: CronField[Int]): CronExpr[Int] =
        Every(step, field)


  // -----------------------------------------------------------
  // CronJob Builder DSL
  // -----------------------------------------------------------
  final class CronBuilder:
    var m: CronExpr[Int] = Every(1, Minute)
    var h: CronExpr[Int] = Every(1, Hour)
    var dom: CronExpr[Int] = Every(1, DayOfMonth)
    var dow: CronExpr[Int] = Every(1, DayOfWeek)

    def build(): CronJobExpr =
      CronJobExpr(m, h, dom, dow)

