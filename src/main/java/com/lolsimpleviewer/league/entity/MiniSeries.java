package com.lolsimpleviewer.league.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MiniSeries {
	private Long target;
	private Long wins;
	private Long losses;
	private String progress;
}
