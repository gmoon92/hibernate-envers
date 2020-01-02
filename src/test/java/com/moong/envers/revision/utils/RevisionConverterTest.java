package com.moong.envers.revision.utils;

import com.moong.envers.approve.domain.Approve;
import com.moong.envers.member.domain.Member;
import com.moong.envers.revision.vo.compare.MemberCompareVO;
import com.moong.envers.team.domain.Team;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
class RevisionConverterTest {

    @Test
    void testSerializedObject() {
        Serializable serializable = 18L;
        byte[] bytes = RevisionConverter.serializedObject(serializable);
        String expected = "-84, -19, 0, 5, 115, 114, 0, 14, 106, 97, 118, 97, 46, 108, 97, 110, 103, 46, 76, 111, 110, 103, 59, -117, -28, -112, -52, -113, 35, -33, 2, 0, 1, 74, 0, 5, 118, 97, 108, 117, 101, 120, 114, 0, 16, 106, 97, 118, 97, 46, 108, 97, 110, 103, 46, 78, 117, 109, 98, 101, 114, -122, -84, -107, 29, 11, -108, -32, -117, 2, 0, 0, 120, 112, 0, 0, 0, 0, 0, 0, 0, 18";
        assertThat(bytes).isEqualTo(getBytes(expected));

        byte[] bytes2 = RevisionConverter.serializedObject(null);
        String nullExpected = "-84, -19, 0, 5, 112";
        assertThat(bytes2).isEqualTo(getBytes(nullExpected));
    }

    /**
     * 저장된 byte array에 해당된 Object의
     * 필드명이 바뀌거나, 새로운 필드값에 대해선 추적 불가능.
     * https://www.baeldung.com/junit-assert-exception
     * @author moong
     */
    @Test
    void testDeSerializedObject() {
        assertThrows(IllegalArgumentException.class, ()
                -> RevisionConverter.deSerializedObject(null));

        String bytesStr = "-84, -19, 0, 5, 115, 114, 0, 14, 106, 97, 118, 97, 46, 108, 97, 110, 103, 46, 76, 111, 110, 103, 59, -117, -28, -112, -52, -113, 35, -33, 2, 0, 1, 74, 0, 5, 118, 97, 108, 117, 101, 120, 114, 0, 16, 106, 97, 118, 97, 46, 108, 97, 110, 103, 46, 78, 117, 109, 98, 101, 114, -122, -84, -107, 29, 11, -108, -32, -117, 2, 0, 0, 120, 112, 0, 0, 0, 0, 0, 0, 0, 18";
        Long id = (Long) RevisionConverter.deSerializedObject(getBytes(bytesStr));
        assertThat(id).isEqualTo(18L);

        String bytesStr2 = "-84, -19, 0, 5, 115, 114, 0, 42, 99, 111, 109, 46, 109, 111, 111, 110, 103, 46, 101, 110, 118, 101, 114, 115, 46, 97, 112, 112, 114, 111, 118, 101, 46, 100, 111, 109, 97, 105, 110, 46, 65, 112, 112, 114, 111, 118, 101, 36, 73, 100, 63, 73, 34, -43, -115, -99, 72, -7, 2, 0, 2, 76, 0, 8, 109, 101, 109, 98, 101, 114, 73, 100, 116, 0, 16, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 76, 111, 110, 103, 59, 76, 0, 6, 116, 101, 97, 109, 73, 100, 113, 0, 126, 0, 1, 120, 112, 115, 114, 0, 14, 106, 97, 118, 97, 46, 108, 97, 110, 103, 46, 76, 111, 110, 103, 59, -117, -28, -112, -52, -113, 35, -33, 2, 0, 1, 74, 0, 5, 118, 97, 108, 117, 101, 120, 114, 0, 16, 106, 97, 118, 97, 46, 108, 97, 110, 103, 46, 78, 117, 109, 98, 101, 114, -122, -84, -107, 29, 11, -108, -32, -117, 2, 0, 0, 120, 112, 0, 0, 0, 0, 0, 0, 0, 8, 115, 113, 0, 126, 0, 3, 0, 0, 0, 0, 0, 0, 0, 2";
        Approve.Id approveId = (Approve.Id) RevisionConverter.deSerializedObject(getBytes(bytesStr2));
        assertThat(Approve.Id.class)
                .isEqualTo(approveId.getClass());

        String byteStrOfError = "-10, -19, 0, 5, 115, 114, 0, 42, 99, 111, 109, 46, 109, 111, 111, 110, 103, 46, 101, 110, 118, 101, 114, 115, 46, 97, 112, 112, 114, 111, 118, 101, 46, 100, 111, 109, 97, 105, 110, 46, 65, 112, 112, 114, 111, 118, 101, 36, 73, 100, 63, 73, 34, -43, -115, -99, 72, -7, 2, 0, 2, 76, 0, 8, 109, 101, 109, 98, 101, 114, 73, 100, 116, 0, 16, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 76, 111, 110, 103, 59, 76, 0, 6, 116, 101, 97, 109, 73, 100, 113, 0, 126, 0, 1, 120, 112, 115, 114, 0, 14, 106, 97, 118, 97, 46, 108, 97, 110, 103, 46, 76, 111, 110, 103, 59, -117, -28, -112, -52, -113, 35, -33, 2, 0, 1, 74, 0, 5, 118, 97, 108, 117, 101, 120, 114, 0, 16, 106, 97, 118, 97, 46, 108, 97, 110, 103, 46, 78, 117, 109, 98, 101, 114, -122, -84, -107, 29, 11, -108, -32, -117, 2, 0, 0, 120, 112, 0, 0, 0, 0, 0, 0, 0, 8, 115, 113, 0, 126, 0, 3, 0, 0, 0, 0, 0, 0, 0, 2";
        Object error = RevisionConverter.deSerializedObject(getBytes(byteStrOfError));
        assertNull(error);

        String byteStrOfNull = "-84, -19, 0, 5, 112";
        Object error2 = RevisionConverter.deSerializedObject(getBytes(byteStrOfNull));
        assertNull(error2);
    }

    private byte[] getBytes(String str) {
        String[] arr = str.split(", ");
        byte[] bytes = new byte[arr.length];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = Byte.parseByte(arr[i]);
        }
        return bytes;
    }

    @Test
    void testGetCompareVO() {
        Member member = Member.newMember("moon", "pa$$word", Team.newTeam("web1"));

        MemberCompareVO vo = RevisionConverter.convertTo(member, MemberCompareVO.class);
        assertThat(vo).isEqualToComparingFieldByField(member);

        assertThrows(IllegalArgumentException.class, () -> RevisionConverter.convertTo(null, MemberCompareVO.class));
    }
}