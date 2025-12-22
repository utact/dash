package com.ssafy.dash.github.application;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class GitHubSubmissionMetadataExtractorTest {

    private final GitHubSubmissionMetadataExtractor extractor = new GitHubSubmissionMetadataExtractor();

    @Test
    void extractParsesBaekjoonHubCommitMessage() {
        String message = "[Bronze V] Title: A+B, Time: 108 ms, Memory: 14212 KB -BaekjoonHub";
        String path = "백준/Bronze/1000.A+B.java";

        GitHubSubmissionMetadataExtractor.SubmissionMetadata metadata = extractor.extract(message, path);

        assertThat(metadata.platform()).isEqualTo("BAEKJOON");
        assertThat(metadata.difficulty()).isEqualTo("Bronze V");
        assertThat(metadata.problemNumber()).isEqualTo("1000");
        assertThat(metadata.title()).isEqualTo("A+B");
        assertThat(metadata.runtimeMs()).isEqualTo(108);
        assertThat(metadata.memoryKb()).isEqualTo(14212);
        assertThat(metadata.language()).isEqualTo("JAVA");
    }

    @Test
    void extractFallsBackWhenCommitMessageMissing() {
        String message = "chore: update solution";
        String path = "백준/Gold/12345.SomeProblem/Main.kt";

        GitHubSubmissionMetadataExtractor.SubmissionMetadata metadata = extractor.extract(message, path);

        assertThat(metadata.platform()).isEqualTo("BAEKJOON");
        assertThat(metadata.difficulty()).isNull();
        assertThat(metadata.problemNumber()).isEqualTo("12345");
        assertThat(metadata.title()).contains("SomeProblem");
        assertThat(metadata.language()).isEqualTo("KOTLIN");
    }

    @Test
    void extractHandlesTitleWithNumbers() {
        String message = "chore: update";
        String path = "백준/Gold/12865.평범한배낭2/Main.java";

        GitHubSubmissionMetadataExtractor.SubmissionMetadata metadata = extractor.extract(message, path);

        assertThat(metadata.platform()).isEqualTo("BAEKJOON");
        assertThat(metadata.problemNumber()).isEqualTo("12865");
        assertThat(metadata.title()).isEqualTo("평범한배낭2");
        assertThat(metadata.language()).isEqualTo("JAVA");
    }

    @Test
    void extractHandles2xnTiling() {
        String message = "solved";
        String path = "백준/Silver/11726.2×n타일링/Main.java";

        GitHubSubmissionMetadataExtractor.SubmissionMetadata metadata = extractor.extract(message, path);

        assertThat(metadata.problemNumber()).isEqualTo("11726");
        assertThat(metadata.title()).isEqualTo("2×n타일링");
    }

}
