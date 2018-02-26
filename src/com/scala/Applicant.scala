package com.scala

class Applicant(val custname: String, val score: Int, val req: Long) extends Serializable {

  val cust : String = custname
  val cscore: Int = score
  val request: Long = req


  def show(): Unit ={
    println("Customer Name: " + cust)
    println("Credit Score: "+ cscore)
  }

  def test():Unit ={
    println(get_name()+" is eligible for the loan")
  }

  def get_credit(): Int ={
  return cscore
  }

  def get_request():Long ={
    return request
  }
  def get_name():String ={
  return cust
  }

  def approved():Unit={
    println("The customer"+ cust +" is approved for the loan.")
  }
}
