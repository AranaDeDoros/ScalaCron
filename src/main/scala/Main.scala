package org.anaradedoros
import domain.Models.*
import domain.Models.Minute.given
object Main {
  def main(args: Array[String]): Unit = {

    import dsl.DSL.CronDSL.*

    val job = CronJobExpr.build { c =>
      c.m = * / 5
      c.h = 12.h
      c.dom = 1.dom
      c.dow = 1.dow
    }

    println(job.explanation)

    println(job)

  }
}
