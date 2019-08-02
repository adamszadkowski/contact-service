package info.szadkowski.contact.template

interface TemplateFormatter {
    fun format(message: Map<String, String>): String
}
