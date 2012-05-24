<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
     xmlns:html="http://www.w3.org/1999/xhtml" xmlns:chordsML="https://raw.github.com/eldur/ChordsML/master/src/main/resources/ChordsML100" xmlns="http://www.w3.org/1999/xhtml" exclude-result-prefixes="html">
	<xsl:output method="text" indent="no" />
	<xsl:strip-space elements="*"/>
	<!--  http://songs.sourceforge.net/songsdoc/songs.html  -->

	
	
	<xsl:template match="/">
<xsl:apply-templates/></xsl:template>
	
	<xsl:template match="chordsML:song">% -- Next Song -- %
\beginsong{<xsl:value-of select="@title" />}[cr={<xsl:value-of select="@copyright" />}, by={<xsl:choose><xsl:when test="@composer"><xsl:value-of select="@composer" /></xsl:when><xsl:otherwise>unknown</xsl:otherwise></xsl:choose>}]



<xsl:apply-templates/>
\endsong



</xsl:template>


<xsl:template match="chordsML:song/text/verse">
<xsl:choose>
<xsl:when test="chord">
\chordson
</xsl:when>
<xsl:otherwise>
\chordsoff
</xsl:otherwise>
</xsl:choose> 

<xsl:choose>
<xsl:when test="@type='chorus'">
\beginchorus <xsl:apply-templates/>
\endchorus
</xsl:when>
<xsl:when test="@type='intro'">
\beginverse* <xsl:apply-templates/>
\endverse
</xsl:when>
<xsl:when test="@type='outro'">
\beginverse* <xsl:apply-templates/>
\endverse
</xsl:when>
<xsl:when test="@type='bridge'">
\beginverse* <xsl:apply-templates/>
\endverse
</xsl:when>
<xsl:otherwise>
\beginverse <xsl:apply-templates/>
\endverse
</xsl:otherwise>
</xsl:choose> 
</xsl:template>




<xsl:template match="chordsML:song/text/comment">

\begin{SBComment}<xsl:apply-templates/>\end{SBComment}

</xsl:template>



<xsl:template match="repeat">\begin{RepeatPar}<xsl:apply-templates/>\end{RepeatPar}</xsl:template>






<xsl:template match="br">\par
</xsl:template>


	
<xsl:template match="chord">\[<xsl:value-of select="@value" />]<xsl:value-of select="." /></xsl:template>
	
</xsl:stylesheet>
