package com.example.hr.shared.domain;

import java.time.Instant;
import java.util.UUID;

public record AuditMetadata(Instant createdAt,
                            Instant updatedAt,
                            UUID createdBy,
                            UUID lastModifiedBy) {
}
