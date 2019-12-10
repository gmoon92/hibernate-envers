package com.moong.envers.applyForm.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
public class ApplyFormVO {

    /**
     * Parameter null check
     *
     * @NotNull 허용[x] null             / 허용 "", " "
     * @NotEmpty 허용[x] null, ""        / 허용 " "
     * @NotBlank 허용[x] null, "", " "
     *
     * @JsonProperty vs @JsonAlias
     *
     * @JsonProperty("onlyOneJsonKey") 단일 키만 허용
     * @JsonAlias({"jsonKey", "JsonKey", "json_key"}) 2.0 부터 등장한 애노테이션으로 복수 키 가능
     * @author moong
     */
    @NotBlank(message = "신청인을 입력하세요.")
    @JsonAlias( { "user_name", "UserName", "userName" })
    private String applyUserName;

    @NotBlank(message = "신청할 부서를 입력하세.")
    @JsonAlias( { "team_name", "TeamName", "teamName" })
    private String applyTeamName;

    private String content;
}
