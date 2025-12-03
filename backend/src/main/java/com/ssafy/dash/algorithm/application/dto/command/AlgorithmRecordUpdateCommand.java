package com.ssafy.dash.algorithm.application.dto.command;

public record AlgorithmRecordUpdateCommand(
        String problemNumber, String title, String language, String code
) {

}
