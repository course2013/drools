package com.scala

import org.apache.spark.{SparkConf, SparkContext}
import org.drools.core.marshalling.impl.ProtobufMessages
import org.kie.api.runtime.{KieContainer, StatelessKieSession}
import org.kie.api.{KieBase, KieServices}
import org.kie.internal.command.CommandFactory
import org.kie.api.io.ResourceType
import org.kie.api.io.ResourceConfiguration
import org.kie.internal.io.ResourceFactory
import org.kie.internal.KnowledgeBase
import org.kie.internal.KnowledgeBaseFactory
import org.kie.internal.builder.DecisionTableConfiguration

import org.kie.internal.builder.DecisionTableInputType
import org.kie.internal.builder.KnowledgeBuilder
import org.kie.internal.builder.KnowledgeBuilderFactory


object Sample {

  def knowledgeBase_XLS(dtable:String):KnowledgeBase={
    val kbuild : KnowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder()

    val dtconf : DecisionTableConfiguration = KnowledgeBuilderFactory.newDecisionTableConfiguration()

    dtconf.setInputType(DecisionTableInputType.XLS)

    kbuild.add(ResourceFactory.newClassPathResource(dtable),ResourceType.DTABLE,dtconf)

    val kbase: KnowledgeBase = KnowledgeBaseFactory.newKnowledgeBase()


    println(kbuild.getKnowledgePackages)

    kbase.addKnowledgePackages(kbuild.getKnowledgePackages)

    return kbase

  }

  def main(args: Array[String]): Unit = {
   <!-- val spark = SparkSession.builder.master("local[*]").getOrCreate() -->

    val conf = new SparkConf().setAppName("Drool test").setMaster("local[*]")

    val sc = new SparkContext(conf)

    val file = "dataset.csv"

    val xls_location = "xls/rules_xls.xls"
    <!--val lines = sc.textFile(file) -->

    val applicants = sc.parallelize(Array(new Applicant("cust1",450,16000),
      new Applicant("cust2",550,10000),
      new Applicant("cust3",750,40000),
      new Applicant("cust4",650,25000),
      new Applicant("cust5",720,15000),
      new Applicant("cust6",732,60000),
      new Applicant("cust7",457,10000),
      new Applicant("cust8",750,40000),
      new Applicant("cust9",650,25000),
      new Applicant("cust10",760,15000),
      new Applicant("cust11",332,60000)
    ))

    val checks = applicants.collect().foreach(a=> a.show())

   // val xls_base : KnowledgeBase = knowledgeBase_XLS(xls_location)

    //print(xls_base)

  //  val bc_rules = sc.broadcast(xls_base)

   // applicants.map(a => applyXLSRules(bc_rules.value,a)).collect()

    <!-- DRL FILE SECTION: -->

    val rules: KieBase = loadRules()

    val bc_rules = sc.broadcast(rules)

    applicants.map(a => applyRules(bc_rules.value, a)).collect()

    <!-- END OF DRL FILE SECTION -->

  }

  def loadRules(): KieBase ={

    val kieServices : KieServices = KieServices.Factory.get()

    //print(kieServices.getCommands())
    val kieContainer : KieContainer = kieServices.getKieClasspathContainer()

    val kB: KieBase = kieContainer.getKieBase()



    return kB

  }

  def applyRules(kieBase: KieBase, a: Applicant): Applicant ={
    val session : StatelessKieSession = kieBase.newStatelessKieSession()
    session.execute(CommandFactory.newInsert(a))
    return a
  }

  def applyXLSRules(kBase: KnowledgeBase, a: Applicant ): Applicant ={
    val session : StatelessKieSession = kBase.newStatelessKieSession()
    session.execute(CommandFactory.newInsert(a))
    return a
  }

}

