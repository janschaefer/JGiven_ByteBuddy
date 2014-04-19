package com.tngtech.jgiven.report.text;

import org.assertj.core.api.Assertions;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.report.model.ReportModel;

public class ThenPlainTextOutput extends Stage<ThenPlainTextOutput> {
    @ExpectedScenarioState
    ReportModel reportModel;

    @ExpectedScenarioState
    String plainTextOutput;

    public ThenPlainTextOutput the_plain_report_contains_the_text( String line ) {
        Assertions.assertThat( plainTextOutput ).contains( line );
        return this;
    }

    public void the_data_table_has_one_line_for_the_arguments_of_each_case() {}

}
