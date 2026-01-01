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

  extension [A](from: CronExpr[A])
    def to(to: CronExpr[A]): CronExpr[A] =
      (from, to) match
        case (At(f), At(t)) => Range(f, t)
        case _ =>
          sys.error("Ranges can only be created from concrete values")
    def ~(_to: CronExpr[A]): CronExpr[A] =
      to(_to)

  private final case class InvalidCronException(errors: List[CronError])
    extends RuntimeException(
      errors.map(_.toString).mkString("\n")
    )

  def cron(block: CronBuilder => Unit): CronJobExpr =
    val b = CronBuilder()
    block(b)
    CronValidator.validate(b.build()) match
      case Right(job) => job
      case Left(errs) => throw InvalidCronException(errs)

  private object CronValidator {

    def validate(job: CronJobExpr): Either[List[CronError], CronJobExpr] = {
      val errors =
        List(
          validateExpr(job.m),
          validateExpr(job.h),
          validateExpr(job.dom),
          validateExpr(job.dow)
        ).flatten

      if (errors.isEmpty) Right(job)
      else Left(errors)
    }

    private def validateExpr(expr: CronExpr[Int]): List[CronError] =
      expr match {

        case At(v) =>
          validateValue(v)

        case Range(from, to) =>
          validateValue(from) ++
            validateValue(to) ++
            validateRangeOrder(from, to)

        case ListExpr(values) =>
          values.flatMap(validateValue)

        case Every(step, field) =>
          val stepErrors =
            if (step <= 0) List(InvalidStep(step)) else Nil
          stepErrors

        case Step(from, step) =>
          val stepErrors =
            if (step <= 0) List(InvalidStep(step)) else Nil
          validateValue(from) ++ stepErrors
      }

    private def validateValue(v: CronValue[Int]): List[CronError] = {
      val field = fieldOf(v)
      if field.range.contains(v.value) &&
        field.pf.isDefinedAt(v.value) &&
        field.pf(v.value)
      then Nil
      else List(
        InvalidRange(v.value, field.range.end)
      )
    }

    private def validateRangeOrder(
                                    from: CronValue[Int],
                                    to: CronValue[Int]
                                  ): List[CronError] =
      if (from.value > to.value)
        List(InvalidRange(from.value, to.value))
      else Nil

    private def fieldOf(v: CronValue[Int]): CronField[Int] = v match
      case _: Minute => Minute
      case _: Hour => Hour
      case _: DayOfMonth => DayOfMonth
      case _: DayOfWeek => DayOfWeek

  }


