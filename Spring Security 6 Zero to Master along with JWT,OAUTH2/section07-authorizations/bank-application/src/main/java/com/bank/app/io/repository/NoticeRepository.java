package com.bank.app.io.repository;

import java.util.List;

import com.bank.app.io.entity.NoticeEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface NoticeRepository extends CrudRepository<NoticeEntity, Long> {
	
	@Query(value = "from NoticeEntity n where CURDATE() BETWEEN n.noticBegDt AND n.noticEndDt")
	List<NoticeEntity> findAllActiveNotices();

}
