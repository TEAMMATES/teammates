<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/shared" prefix="shared"%>
<shared:feedbackResponseComment frc="${data.comment}"
                                firstIndex="${data.commentIds[0]}"
                                secondIndex="${data.commentIds[1]}"
                                thirdIndex="${data.commentIds[2]}"
                                frcIndex="${data.commentIds[3]}" />