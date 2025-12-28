package org.anaradedoros.scalacron
package dsl

import domain.Models.*

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

  extension (job: CronJobExpr)
    def run(cmd: String): CronJobExpr =
      job.withCommand(cmd)
    infix def >>(cmd: String): CronJobExpr =
      job.withCommand(cmd)
    def ? : String = job.schedule

  extension (dow: java.time.DayOfWeek)
    private def toCron: CronExpr[Int] = At(DayOfWeek.unsafe(dow.getValue % 6))

  extension (c: CronBuilder)
    def minute(expr: CronExpr[Int]): Unit = c.m = expr
    def hour(expr: CronExpr[Int]): Unit = c.h = expr
    def dom(expr: CronExpr[Int]): Unit = c.dom = expr
    def dow(d: java.time.DayOfWeek): Unit = c.dow = d.toCron

  def cron(block: CronBuilder => Unit): CronJobExpr =
    val b = CronBuilder()
    block( b)
    b.build()

