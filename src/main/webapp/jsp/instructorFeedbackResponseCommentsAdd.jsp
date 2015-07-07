<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/shared" prefix="shared"%>
<shared:feedbackResponseComment frc="${data.comment}"
                                firstIndex="${data.commentIdFirstIndex}"
                                secondIndex="${data.commentIdSecondIndex}"
                                thirdIndex="${data.commentIdThirdIndex}"
                                frcIndex="${data.commentIdFrcIndex}"
                                googleId="${data.account.googleId}"/>