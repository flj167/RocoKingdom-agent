-- 创建mysql对话存储表
CREATE TABLE chat_memory_message
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    conversation_id VARCHAR(128) NOT NULL,
    message_index   BIGINT       NOT NULL,
    message_type    VARCHAR(32)  NOT NULL,
    message_text    TEXT         NOT NULL,
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_conversation_id_idx (conversation_id, message_index)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;