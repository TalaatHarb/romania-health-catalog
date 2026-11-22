package net.talaatharb.healthcatalog.dto;

import java.util.UUID;

import net.talaatharb.healthcatalog.model.CatalogImportEntity.Status;

public record CatalogImportDto(UUID id, Status status, HealthCatalogVersionDto version, String error) {
}
