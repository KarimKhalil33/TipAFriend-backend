-- Allow system messages (sender_id can be NULL) and link a message to a task for context pills.

ALTER TABLE messages
    ALTER COLUMN sender_id DROP NOT NULL;

ALTER TABLE messages
    ADD COLUMN IF NOT EXISTS task_assignment_id BIGINT
        REFERENCES task_assignments(id) ON DELETE SET NULL;

ALTER TABLE messages
    ADD COLUMN IF NOT EXISTS is_system BOOLEAN NOT NULL DEFAULT FALSE;

CREATE INDEX IF NOT EXISTS idx_messages_task_assignment
    ON messages(task_assignment_id);

