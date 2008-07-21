<?php

// Start of xml v.

/**
 * Create an XML parser
 * @link http://php.net/manual/en/function.xml-parser-create.php
 * @param encoding string[optional] <p>
 * The optional encoding specifies the character
 * encoding for the input/output in PHP 4. Starting from PHP 5, the input
 * encoding is automatically detected, so that the
 * encoding parameter specifies only the output
 * encoding. In PHP 4, the default output encoding is the same as the
 * input charset. If empty string is passed, the parser attempts to identify
 * which encoding the document is encoded in by looking at the heading 3 or
 * 4 bytes. In PHP 5.0.0 and 5.0.1, the default output charset is
 * ISO-8859-1, while in PHP 5.0.2 and upper is UTF-8. The supported
 * encodings are ISO-8859-1, UTF-8 and
 * US-ASCII.
 * </p>
 * @return resource a resource handle for the new XML parser.
 * </p>
 */
function xml_parser_create ($encoding = null) {}

/**
 * Create an XML parser with namespace support
 * @link http://php.net/manual/en/function.xml-parser-create-ns.php
 * @param encoding string[optional] <p>
 * The optional encoding specifies the character
 * encoding for the input/output in PHP 4. Starting from PHP 5, the input
 * encoding is automatically detected, so that the
 * encoding parameter specifies only the output
 * encoding. In PHP 4, the default output encoding is the same as the
 * input charset. In PHP 5.0.0 and 5.0.1, the default output charset is
 * ISO-8859-1, while in PHP 5.0.2 and upper is UTF-8. The supported
 * encodings are ISO-8859-1, UTF-8 and
 * US-ASCII.
 * </p>
 * @param separator string[optional] <p>
 * With a namespace aware parser tag parameters passed to the various
 * handler functions will consist of namespace and tag name separated by
 * the string specified in seperator or
 * ':' by default. 
 * </p>
 * @return resource a resource handle for the new XML parser.
 * </p>
 */
function xml_parser_create_ns ($encoding = null, $separator = null) {}

/**
 * Use XML Parser within an object
 * @link http://php.net/manual/en/function.xml-set-object.php
 * @param parser resource <p>
 * </p>
 * @param object object <p>
 * </p>
 * @return bool &return.success;
 * </p>
 */
function xml_set_object ($parser, &$object) {}

/**
 * Set up start and end element handlers
 * @link http://php.net/manual/en/function.xml-set-element-handler.php
 * @param parser resource <p>
 * </p>
 * @param start_element_handler callback <p>
 * The function named by start_element_handler
 * must accept three parameters:
 * start_element_handler
 * resourceparser
 * stringname
 * arrayattribs
 * parser 
 * The first parameter, parser, is a
 * reference to the XML parser calling the handler.
 * @param end_element_handler callback <p>
 * The function named by end_element_handler
 * must accept two parameters:
 * end_element_handler
 * resourceparser
 * stringname
 * parser 
 * The first parameter, parser, is a
 * reference to the XML parser calling the handler.
 * @return bool &return.success;
 * </p>
 */
function xml_set_element_handler ($parser, $start_element_handler, $end_element_handler) {}

/**
 * Set up character data handler
 * @link http://php.net/manual/en/function.xml-set-character-data-handler.php
 * @param parser resource <p>
 * </p>
 * @param handler callback <p>
 * handler is a string containing the name of a
 * function that must exist when xml_parse is called
 * for parser.
 * </p>
 * <p>
 * The function named by handler must accept
 * two parameters:
 * handler
 * resourceparser
 * stringdata
 * parser
 * The first parameter, parser, is a
 * reference to the XML parser calling the handler.
 * @return bool &return.success;
 * </p>
 */
function xml_set_character_data_handler ($parser, $handler) {}

/**
 * Set up processing instruction (PI) handler
 * @link http://php.net/manual/en/function.xml-set-processing-instruction-handler.php
 * @param parser resource <p>
 * </p>
 * @param handler callback <p>
 * handler is a string containing the name of a
 * function that must exist when xml_parse is called
 * for parser.
 * </p>
 * <p>
 * The function named by handler must accept
 * three parameters:
 * handler
 * resourceparser
 * stringtarget
 * stringdata
 * parser
 * The first parameter, parser, is a
 * reference to the XML parser calling the handler.
 * @return bool &return.success;
 * </p>
 */
function xml_set_processing_instruction_handler ($parser, $handler) {}

/**
 * Set up default handler
 * @link http://php.net/manual/en/function.xml-set-default-handler.php
 * @param parser resource <p>
 * </p>
 * @param handler callback <p>
 * handler is a string containing the name of a
 * function that must exist when xml_parse is called
 * for parser.
 * </p>
 * <p>
 * The function named by handler must accept
 * two parameters:
 * handler
 * resourceparser
 * stringdata
 * parser
 * The first parameter, parser, is a
 * reference to the XML parser calling the handler.
 * @return bool &return.success;
 * </p>
 */
function xml_set_default_handler ($parser, $handler) {}

/**
 * Set up unparsed entity declaration handler
 * @link http://php.net/manual/en/function.xml-set-unparsed-entity-decl-handler.php
 * @param parser resource <p>
 * </p>
 * @param handler callback <p>
 * handler is a string containing the name of a
 * function that must exist when xml_parse is called
 * for parser.
 * </p>
 * <p>
 * The function named by handler must accept six
 * parameters:
 * handler
 * resourceparser
 * stringentity_name
 * stringbase
 * stringsystem_id
 * stringpublic_id
 * stringnotation_name
 * parser
 * The first parameter, parser, is a
 * reference to the XML parser calling the
 * handler.
 * @return bool &return.success;
 * </p>
 */
function xml_set_unparsed_entity_decl_handler ($parser, $handler) {}

/**
 * Set up notation declaration handler
 * @link http://php.net/manual/en/function.xml-set-notation-decl-handler.php
 * @param parser resource <p>
 * </p>
 * @param handler callback <p>
 * handler is a string containing the name of a
 * function that must exist when xml_parse is called
 * for parser.
 * </p>
 * <p>
 * The function named by handler must accept
 * five parameters:
 * handler
 * resourceparser
 * stringnotation_name
 * stringbase
 * stringsystem_id
 * stringpublic_id
 * parser
 * The first parameter, parser, is a
 * reference to the XML parser calling the handler.
 * @return bool &return.success;
 * </p>
 */
function xml_set_notation_decl_handler ($parser, $handler) {}

/**
 * Set up external entity reference handler
 * @link http://php.net/manual/en/function.xml-set-external-entity-ref-handler.php
 * @param parser resource 
 * @param handler callback <p>
 * handler is a string containing the name of a
 * function that must exist when xml_parse is called
 * for parser.
 * </p>
 * <p>
 * The function named by handler must accept
 * five parameters, and should return an integer value.If the
 * value returned from the handler is false (which it will be if no
 * value is returned), the XML parser will stop parsing and
 * xml_get_error_code will return
 * XML_ERROR_EXTERNAL_ENTITY_HANDLING.
 * handler
 * resourceparser
 * stringopen_entity_names
 * stringbase
 * stringsystem_id
 * stringpublic_id
 * parser
 * The first parameter, parser, is a
 * reference to the XML parser calling the handler.
 * @return bool &return.success;
 * </p>
 */
function xml_set_external_entity_ref_handler ($parser, $handler) {}

/**
 * Set up start namespace declaration handler
 * @link http://php.net/manual/en/function.xml-set-start-namespace-decl-handler.php
 * @param parser resource <p>
 * A reference to the XML parser.
 * </p>
 * @param handler callback <p>
 * handler is a string containing the name of a
 * function that must exist when xml_parse is called
 * for parser.
 * </p>
 * <p>
 * The function named by handler must accept
 * four parameters, and should return an integer value. If the
 * value returned from the handler is false (which it will be if no
 * value is returned), the XML parser will stop parsing and
 * xml_get_error_code will return
 * XML_ERROR_EXTERNAL_ENTITY_HANDLING.
 * handler
 * resourceparser
 * stringuser_data
 * stringprefix
 * stringuri
 * parser
 * The first parameter, parser, is a
 * reference to the XML parser calling the handler.
 * @return bool &return.success;
 * </p>
 */
function xml_set_start_namespace_decl_handler ($parser, $handler) {}

/**
 * Set up end namespace declaration handler
 * @link http://php.net/manual/en/function.xml-set-end-namespace-decl-handler.php
 * @param parser resource <p>
 * A reference to the XML parser.
 * </p>
 * @param handler callback <p>
 * handler is a string containing the name of a
 * function that must exist when xml_parse is called
 * for parser.
 * </p>
 * <p>
 * The function named by handler must accept
 * three parameters, and should return an integer value. If the
 * value returned from the handler is false (which it will be if no
 * value is returned), the XML parser will stop parsing and
 * xml_get_error_code will return
 * XML_ERROR_EXTERNAL_ENTITY_HANDLING.
 * handler
 * resourceparser
 * stringuser_data
 * stringprefix
 * parser
 * The first parameter, parser, is a
 * reference to the XML parser calling the handler.
 * @return bool &return.success;
 * </p>
 */
function xml_set_end_namespace_decl_handler ($parser, $handler) {}

/**
 * Start parsing an XML document
 * @link http://php.net/manual/en/function.xml-parse.php
 * @param parser resource <p>
 * A reference to the XML parser to use.
 * </p>
 * @param data string <p>
 * Chunk of data to parse. A document may be parsed piece-wise by
 * calling xml_parse several times with new data,
 * as long as the is_final parameter is set and
 * true when the last data is parsed.
 * </p>
 * @param is_final bool[optional] <p>
 * If set and true, data is the last piece of
 * data sent in this parse.
 * </p>
 * @return int 1 on success or 0 on failure.
 * </p>
 * <p>
 * For unsuccessful parses, error information can be retrieved with
 * xml_get_error_code,
 * xml_error_string,
 * xml_get_current_line_number,
 * xml_get_current_column_number and
 * xml_get_current_byte_index.
 * </p>
 * <p>
 * Entity errors are reported at the end of the data thus only if
 * is_final is set and true.
 * </p>
 */
function xml_parse ($parser, $data, $is_final = null) {}

/**
 * Parse XML data into an array structure
 * @link http://php.net/manual/en/function.xml-parse-into-struct.php
 * @param parser resource <p>
 * </p>
 * @param data string <p>
 * </p>
 * @param values array <p>
 * </p>
 * @param index array[optional] <p>
 * </p>
 * @return int xml_parse_into_struct returns 0 for failure and 1 for
 * success. This is not the same as false and true, be careful with
 * operators such as ===.
 * </p>
 */
function xml_parse_into_struct ($parser, $data, array &$values, array &$index = null) {}

/**
 * Get XML parser error code
 * @link http://php.net/manual/en/function.xml-get-error-code.php
 * @param parser resource <p>
 * A reference to the XML parser to get error code from.
 * </p>
 * @return int This function returns false if parser does
 * not refer to a valid parser, or else it returns one of the error
 * codes listed in the error codes
 * section.
 * </p>
 */
function xml_get_error_code ($parser) {}

/**
 * Get XML parser error string
 * @link http://php.net/manual/en/function.xml-error-string.php
 * @param code int <p>
 * An error code from xml_get_error_code.
 * </p>
 * @return string a string with a textual description of the error
 * code, or false if no description was found.
 * </p>
 */
function xml_error_string ($code) {}

/**
 * Get current line number for an XML parser
 * @link http://php.net/manual/en/function.xml-get-current-line-number.php
 * @param parser resource <p>
 * A reference to the XML parser to get line number from.
 * </p>
 * @return int This function returns false if parser does
 * not refer to a valid parser, or else it returns which line the
 * parser is currently at in its data buffer.
 * </p>
 */
function xml_get_current_line_number ($parser) {}

/**
 * Get current column number for an XML parser
 * @link http://php.net/manual/en/function.xml-get-current-column-number.php
 * @param parser resource <p>
 * A reference to the XML parser to get column number from.
 * </p>
 * @return int This function returns false if parser does
 * not refer to a valid parser, or else it returns which column on
 * the current line (as given by
 * xml_get_current_line_number) the parser is
 * currently at.
 * </p>
 */
function xml_get_current_column_number ($parser) {}

/**
 * Get current byte index for an XML parser
 * @link http://php.net/manual/en/function.xml-get-current-byte-index.php
 * @param parser resource <p>
 * A reference to the XML parser to get byte index from.
 * </p>
 * @return int This function returns false if parser does
 * not refer to a valid parser, or else it returns which byte index
 * the parser is currently at in its data buffer (starting at 0).
 * </p>
 */
function xml_get_current_byte_index ($parser) {}

/**
 * Free an XML parser
 * @link http://php.net/manual/en/function.xml-parser-free.php
 * @param parser resource A reference to the XML parser to free.
 * @return bool This function returns false if parser does not
 * refer to a valid parser, or else it frees the parser and returns true.
 * </p>
 */
function xml_parser_free ($parser) {}

/**
 * Set options in an XML parser
 * @link http://php.net/manual/en/function.xml-parser-set-option.php
 * @param parser resource <p>
 * A reference to the XML parser to set an option in.
 * </p>
 * @param option int <p>
 * Which option to set. See below.
 * </p>
 * <p>
 * The following options are available:
 * <table>
 * XML parser options
 * <tr valign="top">
 * <td>Option constant</td>
 * <td>Data type</td>
 * <td>Description</td>
 * </tr>
 * <tr valign="top">
 * <td>XML_OPTION_CASE_FOLDING</td>
 * <td>integer</td>
 * <td>
 * Controls whether case-folding is enabled for this
 * XML parser. Enabled by default.
 * </td>
 * </tr>
 * <tr valign="top">
 * <td>XML_OPTION_SKIP_TAGSTART</td>
 * <td>integer</td> 
 * <td>
 * Specify how many characters should be skipped in the beginning of a
 * tag name.
 * </td>
 * </tr>
 * <tr valign="top">
 * <td>XML_OPTION_SKIP_WHITE</td>
 * <td>integer</td> 
 * <td>
 * Whether to skip values consisting of whitespace characters.
 * </td>
 * </tr>
 * <tr valign="top">
 * <td>XML_OPTION_TARGET_ENCODING</td>
 * <td>string</td> 
 * <td>
 * Sets which target encoding to
 * use in this XML parser.By default, it is set to the same as the
 * source encoding used by xml_parser_create.
 * Supported target encodings are ISO-8859-1,
 * US-ASCII and UTF-8.
 * </td>
 * </tr>
 * </table>
 * </p>
 * @param value mixed <p>
 * The option's new value.
 * </p>
 * @return bool This function returns false if parser does not
 * refer to a valid parser, or if the option could not be set. Else the
 * option is set and true is returned.
 * </p>
 */
function xml_parser_set_option ($parser, $option, $value) {}

/**
 * Get options from an XML parser
 * @link http://php.net/manual/en/function.xml-parser-get-option.php
 * @param parser resource A reference to the XML parser to get an option from.
 * @param option int Which option to fetch. XML_OPTION_CASE_FOLDING
 * and XML_OPTION_TARGET_ENCODING are available.
 * See xml_parser_set_option for their description.
 * @return mixed This function returns false if parser does
 * not refer to a valid parser or if option isn't
 * valid (generates also a E_WARNING).
 * Else the option's value is returned.
 * </p>
 */
function xml_parser_get_option ($parser, $option) {}

/**
 * Encodes an ISO-8859-1 string to UTF-8
 * @link http://php.net/manual/en/function.utf8-encode.php
 * @param data string <p>
 * An ISO-8859-1 string.
 * </p>
 * @return string the UTF-8 translation of data.
 * </p>
 */
function utf8_encode ($data) {}

/**
 * Converts a string with ISO-8859-1 characters encoded with UTF-8
   to single-byte ISO-8859-1
 * @link http://php.net/manual/en/function.utf8-decode.php
 * @param data string <p>
 * An UTF-8 encoded string.
 * </p>
 * @return string the ISO-8859-1 translation of data.
 * </p>
 */
function utf8_decode ($data) {}

define ('XML_ERROR_NONE', 0);
define ('XML_ERROR_NO_MEMORY', 1);
define ('XML_ERROR_SYNTAX', 2);
define ('XML_ERROR_NO_ELEMENTS', 3);
define ('XML_ERROR_INVALID_TOKEN', 4);
define ('XML_ERROR_UNCLOSED_TOKEN', 5);
define ('XML_ERROR_PARTIAL_CHAR', 6);
define ('XML_ERROR_TAG_MISMATCH', 7);
define ('XML_ERROR_DUPLICATE_ATTRIBUTE', 8);
define ('XML_ERROR_JUNK_AFTER_DOC_ELEMENT', 9);
define ('XML_ERROR_PARAM_ENTITY_REF', 10);
define ('XML_ERROR_UNDEFINED_ENTITY', 11);
define ('XML_ERROR_RECURSIVE_ENTITY_REF', 12);
define ('XML_ERROR_ASYNC_ENTITY', 13);
define ('XML_ERROR_BAD_CHAR_REF', 14);
define ('XML_ERROR_BINARY_ENTITY_REF', 15);
define ('XML_ERROR_ATTRIBUTE_EXTERNAL_ENTITY_REF', 16);
define ('XML_ERROR_MISPLACED_XML_PI', 17);
define ('XML_ERROR_UNKNOWN_ENCODING', 18);
define ('XML_ERROR_INCORRECT_ENCODING', 19);
define ('XML_ERROR_UNCLOSED_CDATA_SECTION', 20);
define ('XML_ERROR_EXTERNAL_ENTITY_HANDLING', 21);
define ('XML_OPTION_CASE_FOLDING', 1);
define ('XML_OPTION_TARGET_ENCODING', 2);
define ('XML_OPTION_SKIP_TAGSTART', 3);
define ('XML_OPTION_SKIP_WHITE', 4);
define ('XML_SAX_IMPL', "libxml");

// End of xml v.
?>
