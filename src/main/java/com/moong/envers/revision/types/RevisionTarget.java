package com.moong.envers.revision.types;

import com.moong.envers.applyForm.domain.ApplyForm;
import com.moong.envers.applyForm.vo.ApplyFormVO;
import com.moong.envers.approve.domain.Approve;
import com.moong.envers.approve.vo.ApproveVO;
import com.moong.envers.common.domain.BaseEntity;
import com.moong.envers.common.vo.EntityCompareVO;
import com.moong.envers.member.domain.Member;
import com.moong.envers.member.vo.MemberCompareVO;
import com.moong.envers.team.domain.Team;
import com.moong.envers.team.vo.TeamCompareVO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.function.Function;

/**
 * Serializable Object -> DataBase save
 * DataBase -> De
 * https://www.javaspecialists.eu/archive/Issue020.html
 * https://stackoverflow.com/questions/26901706/how-to-save-a-serialized-object-in-a-database
 * */
@Slf4j
@Getter
public enum RevisionTarget {

     MEMBER(Member.class
            , MemberCompareVO.class
            , RevisionTarget::serializableObjectStr
            , RevisionTarget::deSerializableObject)
    ,TEAM(Team.class
            , TeamCompareVO.class
            , RevisionTarget::serializableObjectStr
            , RevisionTarget::deSerializableObject)
    , APPROVE(Approve.class
            , ApproveVO.class
            , RevisionTarget::serializableObjectStr
            , RevisionTarget::deSerializableObject)
    ,APPLY_FORM(ApplyForm.class
            , ApplyFormVO.class
            , RevisionTarget::serializableObjectStr
            , RevisionTarget::deSerializableObject)
    ;

    private final Class<? extends BaseEntity> entityClass;
    private final Class<? extends EntityCompareVO> compareVOClass;
    private final Function<Serializable, String> convertToRevisionEntityIDExpression;
    private final Function<String, Object> convertToEntityIDExpression;

    RevisionTarget(Class<? extends BaseEntity> entityClass, Class<? extends EntityCompareVO> compareVOClass, Function<Serializable, String> convertToRevisionEntityIDExpression, Function<String, Object> convertToEntityIDExpression) {
        this.entityClass = entityClass;
        this.compareVOClass = compareVOClass;
        this.convertToRevisionEntityIDExpression = convertToRevisionEntityIDExpression;
        this.convertToEntityIDExpression = convertToEntityIDExpression;
    }

    public static String serializableObjectStr(Serializable entityId) {
        byte[] bytes = {};
        StringBuffer sb = new StringBuffer();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(entityId);
            out.flush();
            bytes = bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Not serialize of entity id string", e);
        } finally {
            for (byte b : bytes) {
                sb.append(b + ",");
            }
            String str = sb.toString();
            str = str.substring(0, str.length() - 1);
            return str;
        }
    }

    public static Object deSerializableObject(String deserializerStr) {
        String[] arr = deserializerStr.split(",");
        byte[] buf = new byte[arr.length];

        for(int i = 0 ; i < buf.length ; ++i) {
            buf[i] = Byte.parseByte(arr[i]);
        }
        Object deSerializedObject = null;
        if (buf != null) {
            try (ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(buf))) {
                deSerializedObject = objectIn.readObject();
                log.info("deSerializedObject : {}", deSerializedObject);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("Not deSerialize of entity id object", e);
            }
        }
        return deSerializedObject;
    }

    public String convertToRevisionEntityID(Serializable entityId) {
       return convertToRevisionEntityIDExpression.apply(entityId);
    }

    public Object convertToEntityID(String revEntityId) {
        return convertToEntityIDExpression.apply(revEntityId);
    }

    public static RevisionTarget of(Class entityClass) {
        return Arrays.stream(RevisionTarget.values())
                .filter(target -> target.getEntityClass().equals(entityClass))
                .findFirst()
                .orElse(null);
    }

    public Object newInstanceCompareVO(Object entity) {
        Object compareVO = null;
        try {
            compareVO = getCompareVOClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("{}", e);
            throw new RuntimeException("Not convert to compare value object...", e);
        }

        BeanUtils.copyProperties(entity, compareVO);
        return compareVO;
    }

    public static <T extends BaseEntity, R extends EntityCompareVO> R newInstanceCompareVO(T entity, Class<R> compareVOClass) {
        Object compareVO = null;
        try {
            compareVO = compareVOClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("{}", e);
            throw new RuntimeException("Not convert to compare value object...", e);
        }

        BeanUtils.copyProperties(entity, compareVO);
        return (R) compareVO;
    }
}
