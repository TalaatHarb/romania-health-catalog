package net.talaatharb.healthcatalog.dto.xml;

import java.util.Date;

import lombok.Data;

@Data public class PersonCategory { 
	private String code;
	private String description;
	private Boolean supportsOverlapping;
	private Boolean definedByAge;
	private String personState;
	private Integer priority;
	private Integer isOptional;
	private Integer canBeReported;
	private Date validFrom;
	private Integer maxDuration;
	private String maxDurationRoundType;
	private Date validTo;
	private Integer restrictedMinAge;
	private Integer restrictedMaxAge;
	private Integer forSex;
}
