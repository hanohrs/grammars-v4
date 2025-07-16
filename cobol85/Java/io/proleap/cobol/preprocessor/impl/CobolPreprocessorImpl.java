/*
 * Copyright (C) 2017, Ulrich Wolffgang <u.wol@wwu.de>
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD 3-clause license. See the LICENSE file for details.
 */

package io.proleap.cobol.preprocessor.impl;

import io.proleap.cobol.preprocessor.CobolPreprocessor;
import io.proleap.cobol.preprocessor.sub.CobolLine;
import io.proleap.cobol.preprocessor.sub.document.impl.CobolDocumentParserImpl;
import io.proleap.cobol.preprocessor.sub.line.reader.impl.CobolLineReaderImpl;
import io.proleap.cobol.preprocessor.sub.line.rewriter.impl.CobolCommentEntriesMarkerImpl;
import io.proleap.cobol.preprocessor.sub.line.rewriter.impl.CobolLineIndicatorProcessorImpl;
import io.proleap.cobol.preprocessor.sub.line.writer.CobolLineWriter;
import io.proleap.cobol.preprocessor.sub.line.writer.impl.CobolLineWriterImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CobolPreprocessorImpl implements CobolPreprocessor {

	private static final Logger LOG = Logger.getLogger(CobolPreprocessorImpl.class.getSimpleName());

	protected CobolCommentEntriesMarkerImpl createCommentEntriesMarker() {
		return new CobolCommentEntriesMarkerImpl();
	}

	protected CobolDocumentParserImpl createDocumentParser(final List<Path> copyFiles) {
		return new CobolDocumentParserImpl(copyFiles);
	}

	protected CobolLineIndicatorProcessorImpl createLineIndicatorProcessor() {
		return new CobolLineIndicatorProcessorImpl();
	}

	protected CobolLineReaderImpl createLineReader() {
		return new CobolLineReaderImpl();
	}

	protected CobolLineWriter createLineWriter() {
		return new CobolLineWriterImpl();
	}

	protected String parseDocument(
			final List<CobolLine> lines, final List<Path> copyFiles,
			final CobolSourceFormatEnum format, final CobolDialect dialect
	) {
		final String code = createLineWriter().serialize(lines);
		return createDocumentParser(copyFiles).processLines(code, format, dialect);
	}

	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.err.println("Usage: CobolPreprocessorImpl file [copybook...]");
			System.exit(1);
		}

		Path cobolFile = Path.of(args[0]);
		List<Path> copyFiles = Arrays.stream(args)
				.skip(1)
				.map(Path::of)
				.collect(Collectors.toList());
		CobolPreprocessor preprocessor = new CobolPreprocessorImpl();
		String result = preprocessor.process(cobolFile, copyFiles, CobolSourceFormatEnum.FIXED);
		System.out.println(result);
	}

	@Override
	public String process(final Path cobolFile, final List<Path> copyFiles, final CobolSourceFormatEnum format)
			throws IOException {
		return process(cobolFile, copyFiles, format, null);
	}

	@Override
	public String process(final Path cobolFile, final List<Path> copyFiles, final CobolSourceFormatEnum format,
						  final CobolDialect dialect) throws IOException {
		LOG.info(String.format("Preprocessing file %s.", cobolFile.getFileName()));
		return process(Files.readString(cobolFile), copyFiles, format, dialect);
	}

	@Override
	public String process(final String cobolSourceCode, final List<Path> copyFiles,
						  final CobolSourceFormatEnum format) {
		return process(cobolSourceCode, copyFiles, format, null);
	}

	@Override
	public String process(final String cobolCode, final List<Path> copyFiles, final CobolSourceFormatEnum format,
						  final CobolDialect dialect) {
		final List<CobolLine> lines = readLines(cobolCode, format, dialect);
		final List<CobolLine> rewrittenLines = rewriteLines(lines);
		final String result = parseDocument(rewrittenLines, copyFiles, format, dialect);

		LOG.fine(String.format("Processed input:\n\n%s\n\n", result));

		return result;
	}

	protected List<CobolLine> readLines(final String cobolCode, final CobolSourceFormatEnum format,
										final CobolDialect dialect) {
		return createLineReader().processLines(cobolCode, format, dialect);
	}

	/**
	 * Normalizes lines of given COBOL source code, so that comment entries can
	 * be parsed and lines have a unified line format.
	 */
	protected List<CobolLine> rewriteLines(final List<CobolLine> lines) {
		final List<CobolLine> lineIndicatorProcessedLines = createLineIndicatorProcessor().processLines(lines);
		return createCommentEntriesMarker().processLines(lineIndicatorProcessedLines);
	}
}
