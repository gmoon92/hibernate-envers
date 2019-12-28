package com.moong.envers.revision.core.utils;

import com.moong.envers.common.domain.BaseEntity;
import com.moong.envers.common.vo.EntityCompareVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@Slf4j
public final class RevisionConverter {

    /**
     * Serializable byte array -> DataBase save
     * https://www.javaspecialists.eu/archive/Issue020.html
     * https://stackoverflow.com/questions/26901706/how-to-save-a-serialized-object-in-a-database
     * @author moong
     * */
    public static byte[] serializedObject(Serializable entityId) {
        byte[] bytes = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(entityId);
            out.flush();
            bytes = bos.toByteArray();
            log.info("serializedObject ({}) : {}", bytes.length, bytes);
        } catch (IOException e) {
            throw new RuntimeException("Entity ID type serialization is not converted to a byte array", e);
        } finally {
            return bytes;
        }
    }

    /**
     * DeSerializable DataBase byte array -> Java Object
     * @author moong
     * */
    public static Object deSerializedObject(byte[] entityId) {
        Object deSerializedObject = null;
        if (entityId == null)
            return deSerializedObject;

        try (ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(entityId))) {
            deSerializedObject = objectIn.readObject();
            log.info("deSerializedObject : {}", deSerializedObject);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Entity ID type byte array is not converted to object", e);
        } finally {
            return deSerializedObject;
        }
    }

    public static <T extends BaseEntity, R extends EntityCompareVO> R ofCompareVO(T entity, Class<R> compareVOClass) {
        Object compareVO;
        try {
            compareVO = compareVOClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("{}", e);
            throw new RuntimeException("Does Not convert to EntityCompareVO object", e);
        }

        BeanUtils.copyProperties(entity, compareVO);
        return (R) compareVO;
    }
}
