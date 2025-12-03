package com.ssafy.dash.algorithm.application.dto.command;

public record AlgorithmRecordCreateCommand(
        Long userId, String problemNumber, String title, String language, String code
) {

}
