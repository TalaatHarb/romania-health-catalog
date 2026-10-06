package net.talaatharb.healthcatalog.api;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import net.talaatharb.healthcatalog.constants.ApiConstants;
import net.talaatharb.healthcatalog.dto.CatalogItemDto;
import net.talaatharb.healthcatalog.dto.CatalogItemTypeDto;
import net.talaatharb.healthcatalog.model.CatalogItemType;

@RequestMapping(ApiConstants.API_V1)
@CrossOrigin
public interface CatalogItemApi {

	@GetMapping(path = ApiConstants.VERSIONS + "/{versionId}" + ApiConstants.ITEM_TYPES, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	List<CatalogItemTypeDto> getItemTypes(@PathVariable UUID versionId);

	@GetMapping(path = ApiConstants.VERSIONS + "/{versionId}" + ApiConstants.ITEMS, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	Page<CatalogItemDto> searchForItems(@PathVariable UUID versionId, @RequestParam CatalogItemType type,
			@RequestParam(defaultValue = "") String searchTerm, Pageable pageable);

	@GetMapping(path = ApiConstants.ITEMS + "/{itemId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	CatalogItemDto getItem(@PathVariable UUID itemId);
}
