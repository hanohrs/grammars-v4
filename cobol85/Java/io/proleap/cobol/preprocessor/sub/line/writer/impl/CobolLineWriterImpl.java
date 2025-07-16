/*
 * Copyright (C) 2017, Ulrich Wolffgang <u.wol@wwu.de>
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD 3-clause license. See the LICENSE file for details.
 */

package io.proleap.cobol.preprocessor.sub.line.writer.impl;

import java.util.List;
import java.util.stream.Collectors;

import io.proleap.cobol.preprocessor.CobolPreprocessor;
import io.proleap.cobol.preprocessor.sub.CobolLine;
import io.proleap.cobol.preprocessor.sub.CobolLineTypeEnum;
import io.proleap.cobol.preprocessor.sub.line.writer.CobolLineWriter;

public class CobolLineWriterImpl implements CobolLineWriter {

	@Override
	public String serialize(List<CobolLine> lines) {
		return lines.stream()
				.map(line -> {
					StringBuilder sb = new StringBuilder();
					if (!CobolLineTypeEnum.CONTINUATION.equals(line.type)) {
						sb.append(line.blankSequenceArea());
						sb.append(line.indicatorArea);
					}
					sb.append(line.getContentArea());
					if (line.type == CobolLineTypeEnum.COMMENT) {
						sb.append(line.comment);
					} else if (line.comment != null && !line.comment.isEmpty()) {
						sb.append(CobolPreprocessor.WS);
						sb.append(CobolPreprocessor.COMMENT_TAG);
						sb.append(line.comment);
					}
					return sb.toString();
				})
				.collect(Collectors.joining(CobolPreprocessor.NEWLINE));
	}
}
