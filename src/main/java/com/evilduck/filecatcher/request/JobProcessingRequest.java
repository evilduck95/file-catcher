package com.evilduck.filecatcher.request;

import java.util.List;

public record JobProcessingRequest(List<String> jobIds) {

}
