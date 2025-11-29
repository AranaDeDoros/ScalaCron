package org.anaradedoros
package domain

import dsl.DSL.CronBuilder

import scala.collection.immutable.NumericRange

object Models :

  // -----------------------------------------------------------
  // Errors
  // -----------------------------------------------------------
  sealed trait CronError
  private final case class RangeError[A](
                                          value: A,
                                          validRange: NumericRange[A],
                                          pf: PartialFunction[A, Boolean]
                                        ) extends CronError


  // -----------------------------------------------------------
  // Cron Value 
  // -----------------------------------------------------------
  trait CronValue[A]:
    def value: A

  final case class Minute(value: Int) extends CronValue[Int]
  final case class Hour(value: Int) extends CronValue[Int]
  final case class DayOfMonth(value: Int) extends CronValue[Int]
  final case class DayOfWeek(value: Int) extends CronValue[Int]


  // -----------------------------------------------------------
  // Cron Field Trait
  // -----------------------------------------------------------
  trait CronField[A]:
    def range: NumericRange[A]
    def pf: PartialFunction[A, Boolean]
    protected def build(value: A): CronValue[A]

    final def applyValidated(value: A): Either[CronError, CronExpr[A]] =
      if range.contains(value) && pf.isDefinedAt(value) && pf(value) then
        Right(At(build(value)))
      else
        Left(RangeError(value, range, pf))

    final def unsafe(value: A): CronValue[A] =
      build(value)


  // -----------------------------------------------------------
  // Concrete Fields
  // -----------------------------------------------------------
  object Minute extends CronField[Int]:
    given CronField[Int] = this
    //val range: scala.collection.immutable.Range.Inclusive = 0 to 59
    val range: NumericRange.Inclusive[Int] = NumericRange.inclusive(0, 59, 1)
    val pf: PartialFunction[Int, Boolean] = { case m if m >= 0 && m <= 59 => true }
    protected def build(v: Int) = Minute(v)

  object Hour extends CronField[Int]:
    given CronField[Int] = this
    val range: NumericRange.Inclusive[Int] = NumericRange.inclusive(0, 23, 1)
    //val range: scala.collection.immutable.Range.Inclusive =  0 to 23
    val pf: PartialFunction[Int, Boolean] = { case h if h >= 0 && h <= 23 => true }
    protected def build(v: Int) = Hour(v)

  object DayOfMonth extends CronField[Int]:
    given CronField[Int] = this
    //val range: scala.collection.immutable.Range.Inclusive  = 1 to 31
    val range: NumericRange.Inclusive[Int] = NumericRange.inclusive(1,31, 1)
    val pf: PartialFunction[Int, Boolean] = { case d if d >= 1 && d <= 31 => true }
    protected def build(v: Int) = DayOfMonth(v)

  object DayOfWeek extends CronField[Int]:
    given CronField[Int] = this
    //val range: scala.collection.immutable.Range.Inclusive  = 0 to 6
    val range: NumericRange.Inclusive[Int] = NumericRange.inclusive(0, 6, 1)
    val pf: PartialFunction[Int, Boolean] = { case d if d >= 0 && d <= 6 => true }
    protected def build(v: Int) = DayOfWeek(v)


  // -----------------------------------------------------------
  // AST Types
  // -----------------------------------------------------------
  sealed trait CronExpr[A]
  final case class Every[A](step: Int, field: CronField[A]) extends CronExpr[A]
  final case class At[A](value: CronValue[A]) extends CronExpr[A]
  final case class Range[A](from: CronValue[A], to: CronValue[A]) extends CronExpr[A]
  final case class ListExpr[A](values: List[CronValue[A]]) extends CronExpr[A]
  final case class Step[A](from: CronValue[A], step: Int) extends CronExpr[A]


  // -----------------------------------------------------------
  // CronJob AST
  // -----------------------------------------------------------
  case class CronJobExpr(
                          m: CronExpr[Int],
                          h: CronExpr[Int],
                          dom: CronExpr[Int],
                          dow: CronExpr[Int]
                        ):
    override def toString: String = CronRender.renderJob(this)
    def explanation : String = CronRender.explain(List(m, h, dom, dow))


  object CronJobExpr:
    def build(init: CronBuilder => Unit): CronJobExpr =
      val b = CronBuilder()
      init(b)
      b.build()

  // -----------------------------------------------------------
  // Rendering
  // -----------------------------------------------------------
  private object CronRender:

    private def render[A](expr: CronExpr[A]): String = expr match
      case At(v) => v.value.toString
      case Every(step, _) => s"*/$step"
      case Range(f, t) => s"${f.value}-${t.value}"
      case ListExpr(vs) => vs.map(_.value).mkString(",")
      case Step(f, s) => s"${f.value}/$s"

    def explain[A](expr: List[CronExpr[A]]): String =
      val explanation = for{
        arg <- expr
      } yield arg match
        case At(v) => s"${v.value.toString}"
        case Every(step, _) => s"every/$step"
        case Range(f, t) => s"from ${f.value} to ${t.value}"
        case ListExpr(vs) => vs.map(_.value).mkString(",")
        case Step(f, s) => s"from ${f.value} with a step of $s"
      explanation.mkString("minute | hour | dom | dow\n", " | ", ".")

    def renderJob(job: CronJobExpr): String =
      s"${render(job.m)} ${render(job.h)} ${render(job.dom)} ${render(job.dow)}"


