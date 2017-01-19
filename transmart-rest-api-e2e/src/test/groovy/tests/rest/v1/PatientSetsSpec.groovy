package tests.rest.v1

import base.RESTSpec
import base.RestCall
import spock.lang.Requires

import static config.Config.EHR_LOADED
import static config.Config.V1_PATH_PATIENT_SETS

@Requires({EHR_LOADED})
class PatientSetsSpec extends RESTSpec {

    /**
     *  given: "study EHR is loaded"
     *  when: "I create a patient set"
     *  then: "a set is created and returned"
     */
    def "v1 post patient set"() {
        given: "study EHR is loaded"
        RestCall testRequest = new RestCall(V1_PATH_PATIENT_SETS, contentTypeForJSON);
        testRequest.statusCode = 201
        testRequest.contentType = contentTypeForXML
        testRequest.body = '<ns4:query_definition xmlns:ns4="http://www.i2b2.org/xsd/cell/crc/psm/1.1/"><specificity_scale>0</specificity_scale><panel><panel_number></panel_number><invert>0</invert><total_item_occurrences>1</total_item_occurrences><item><item_name>Heart Rate</item_name><item_key>\\\\Vital Signs\\Vital Signs\\Heart Rate\\</item_key><tooltip>\\Vital Signs\\Heart Rate\\</tooltip><hlevel>1</hlevel><class>ENC</class></item><panel_timing>ANY</panel_timing></panel><panel><panel_number>2</panel_number><invert>0</invert><total_item_occurrences>1</total_item_occurrences><item><item_name>Age</item_name><item_key>\\\\Public Studies\\Public Studies\\SHARED_CONCEPTS_STUDY_A\\Demography\\Age\\</item_key><tooltip>\\Public Studies\\SHARED_CONCEPTS_STUDY_A\\Demography\\Age\\</tooltip><hlevel>3</hlevel><class>ENC</class><constrain_by_value><value_operator>LT</value_operator><value_constraint>70</value_constraint><value_unit_of_measure>ratio</value_unit_of_measure><value_type>NUMBER</value_type></constrain_by_value></item><panel_timing>ANY</panel_timing></panel><query_timing>ANY</query_timing></ns4:query_definition>'
        when: "I create a patient set"
        def responseData = post(testRequest)

        then: "a set is created and returned"
        assert responseData.id != null
        assert responseData.patients.size() == 2
        responseData.patients.each {
            assert [-67,-57].contains(it.id)
        }
    }

    /**
     *  given: "study EHR is loaded"
     *  when: "I get a patient set by id"
     *  then: "a set is returned"
     */
    def "v1 get patient set"() {
        given: "study EHR is loaded"
        RestCall setup = new RestCall(V1_PATH_PATIENT_SETS, contentTypeForJSON);
        setup.statusCode = 201
        setup.contentType = contentTypeForXML
        setup.body = '<ns4:query_definition xmlns:ns4="http://www.i2b2.org/xsd/cell/crc/psm/1.1/"><specificity_scale>0</specificity_scale><panel><panel_number></panel_number><invert>0</invert><total_item_occurrences>1</total_item_occurrences><item><item_name>Heart Rate</item_name><item_key>\\\\Vital Signs\\Vital Signs\\Heart Rate\\</item_key><tooltip>\\Vital Signs\\Heart Rate\\</tooltip><hlevel>1</hlevel><class>ENC</class></item><panel_timing>ANY</panel_timing></panel><panel><panel_number>2</panel_number><invert>0</invert><total_item_occurrences>1</total_item_occurrences><item><item_name>Age</item_name><item_key>\\\\Public Studies\\Public Studies\\SHARED_CONCEPTS_STUDY_A\\Demography\\Age\\</item_key><tooltip>\\Public Studies\\SHARED_CONCEPTS_STUDY_A\\Demography\\Age\\</tooltip><hlevel>3</hlevel><class>ENC</class><constrain_by_value><value_operator>LT</value_operator><value_constraint>70</value_constraint><value_unit_of_measure>ratio</value_unit_of_measure><value_type>NUMBER</value_type></constrain_by_value></item><panel_timing>ANY</panel_timing></panel><query_timing>ANY</query_timing></ns4:query_definition>'
        def id = post(setup).id

        RestCall testRequest = new RestCall(V1_PATH_PATIENT_SETS + "/${id}", contentTypeForJSON);

        when: "I get a patient set by id"
        def responseData = get(testRequest)

        then: "a set is returned"
        assert responseData.id == id
        assert responseData.patients.size() == 2
        responseData.patients.each {
            assert [-67,-57].contains(it.id)
        }
    }

}