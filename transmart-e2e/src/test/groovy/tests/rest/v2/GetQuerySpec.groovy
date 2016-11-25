package tests.rest.v2

import base.RESTSpec
import groovy.json.JsonBuilder

import static org.hamcrest.Matchers.everyItem
import static org.hamcrest.Matchers.hasEntry
import static org.hamcrest.Matchers.is
import static spock.util.matcher.HamcrestSupport.that
import static Operator.*
import static QueryType.*
import static constraints.*
import static ValueType.*

class GetQuerySpec extends RESTSpec{

    def final INVALIDARGUMENTEXCEPTION = "InvalidArgumentsException"
    def final EMPTYCONTSTRAINT = "Empty constraint parameter."

    /**
     *  when:" I do a Get query/observations with a wrong type."
     *  then: "then I get a 400 with 'Constraint not supported: BadType.'"
     */
    def "Get /query/observations malformed query"(){
        when:" I do a Get query/observations with a wrong type."
        def constraintMap = [type: 'BadType']

        def responseData = get("query/observations", contentTypeForJSON, toQuery(constraintMap))

        then: "then I get a 400 with 'Constraint not supported: BadType.'"
        that responseData.httpStatus, is(400)
        that responseData.type, is(INVALIDARGUMENTEXCEPTION)
        that responseData.message, is('Constraint not supported: BadType.')
    }

    /**
     *  when: "I do a GET /query/observations with a TrueConstraint"
     *  then: "I get all observations."
     */
    def "GET /query/observations with TrueConstraint."(){
        when: "I do a GET /query/observations with a TrueConstraint"
        def constraintMap = [type: TrueConstraint]

        def responseData = get("query/observations", contentTypeForJSON, toQuery(constraintMap))

        then: "all observations are returned."
        println(responseData)
        assert responseData.size() > 1000
    }

    /**
     *  when: "I do a GET /query/aggregate with ConceptConstraint and min"
     *  then: "the minimum value for that concept is returned"
     */
    def "GET /query/aggregate with ConceptConstraint and min"(){
        when: "I do a GET /query/aggregate with ConceptConstraint and min"
        def query =
                [
                constraint: toJSON([
                        type: ConceptConstraint,
                        path:"\\Public Studies\\TEST_17_1\\Vital Signs\\Height CM\\"
                ]),
                type: MIN
        ]
        def responseData = get("query/aggregate", contentTypeForJSON, query)

        then: "the minimum value for that concept is returned"
        assert responseData.min == 163.0
    }

    /**
     *  when: "I do a GET /query/aggregate with ConceptConstraint and max"
     *  then: "the maximum value for that concept is returned"
     */
    def "GET /query/aggregate with ConceptConstraint and max"(){
        when: "I do a GET /query/aggregate with ConceptConstraint and max"
        def query = [
                constraint: toJSON([
                        type: ConceptConstraint,
                        path:"\\Public Studies\\TEST_17_1\\Vital Signs\\Height CM\\"
                ]),
                type: MAX
        ]
        def responseData = get("query/aggregate", contentTypeForJSON, query)

        then: "the maximum value for that concept is returned"
        assert responseData.max == 186.0
    }

    /**
     *  when: "I do a GET /query/observations with a ${AND} Combination Constraint"
     *  then: "all results that match the combination of arguments are returned"
     */
    def "Get /query/observations AND"(){

        when: "I do a GET /query/observations with a ${AND} Combination Constraint"
        def constraintMap = [
                        type: Combination,
                        operator: AND,
                        args: [
                         [type: ConceptConstraint, path:"\\Public Studies\\TEST_17_1\\Vital Signs\\Height CM\\"],
                         [type: ValueConstraint, valueType: NUMERIC, operator: GREATER_THAN, value:176]
                        ]
                ]
        def responseData = get("query/observations", contentTypeForJSON, toQuery(constraintMap))

        then: "all results that match the combination of arguments are returned"
        that responseData.size(), is(28)
    }

    /**
     *  when: " I Get /query/observations PatientSetConstraint with one patient"
     *  then: "I get all observations related to that patient"
     */
    def "/query/observations PatientSetConstraint with one patient"(){
        def id = -62

        when: " I Get /query/observations PatientSetConstraint with one patient"
        def constraintMap =  [
                        type: PatientSetConstraint,
                        patientSetId: 0,
                        patientIds: [id]
                ]
        def responseData = get("query/observations", contentTypeForJSON, toQuery(constraintMap))

        then: "I get all observations related to that patient"
        assert responseData.size() == 4
        that responseData, everyItem(
                hasEntry(is('patient'),
                        hasEntry(is('id'), is(id))
                )
        )
    }
}