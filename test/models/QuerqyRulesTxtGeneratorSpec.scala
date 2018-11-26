package models

import models.FeatureToggleModel.FeatureToggleService
import models.SearchManagementModel.{DeleteRule, FilterRule, SearchInput, UpDownRule}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}

class QuerqyRulesTxtGeneratorSpec extends FlatSpec with Matchers with MockitoSugar {

  val searchManagementRepository = mock[SearchManagementRepository]
  val featureToggleService = mock[FeatureToggleService]

  val generator = new QuerqyRulesTxtGenerator(searchManagementRepository, featureToggleService)

  "Rules Text Generation" should "consider up/down rules correctly" in {
    val upDownRules = List(
      UpDownRule(None, 0, 10, "notebook", true),
      UpDownRule(None, 0, 10, "lenovo", false),
      UpDownRule(None, 1, 10, "battery", true)
    )
    val rulesTxt = generator.renderSearchInputRulesForTerm("laptop", SearchInput(term = "laptop", upDownRules = upDownRules))

    rulesTxt should be(
      s"""|laptop =>
          |\tUP(10): notebook
          |\tDOWN(10): battery
          |""".stripMargin)
  }


  "Rules Text Generation" should "correctly write a DELETE rules" in {
    val deleteRules = List (DeleteRule(None, "freddy", true))

    val rulesTxt  = generator.renderSearchInputRulesForTerm("queen", SearchInput(term = "queen", deleteRules = deleteRules))
    rulesTxt should be(
      s"""|queen =>
          |\tDELETE: freddy
          |""".stripMargin)
  }


  "Rules Text Generation" should "correctly add FILTER rules" in {
    val filterRules = List (FilterRule(None, "zz top", true))

    val rulesTxt  = generator.renderSearchInputRulesForTerm("abba", SearchInput(term = "abba", filterRules = filterRules))
    rulesTxt should be(
      s"""|abba =>
          |\tFILTER: zz top
          |""".stripMargin)
  }

  "Rules Text Generation" should "correctly combine FILTER, DELETE and UPDOWN Rules" in {
    val upDownRules = List(
      UpDownRule(None, 0, 10, "notebook", true),
      UpDownRule(None, 0, 10, "lenovo", false),
      UpDownRule(None, 1, 10, "battery", true)
    )
    val deleteRules = List (DeleteRule(None, "freddy", true))
    val filterRules = List (FilterRule(None, "zz top", true))
    val rulesTxt  = generator.renderSearchInputRulesForTerm("aerosmith",
      SearchInput(term = "aerosmith", filterRules = filterRules, deleteRules = deleteRules, upDownRules = upDownRules))

    rulesTxt should be(
      s"""|aerosmith =>
          |\tUP(10): notebook
          |\tDOWN(10): battery
          |\tFILTER: zz top
          |\tDELETE: freddy
          |""".stripMargin
    )

  }



}
