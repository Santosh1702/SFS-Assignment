{{/* Common labels */}}
{{- define "sfs-assessment-app.labels" -}}
helm.sh/chart: {{ include "sfs-assessment-app.chart" . }}
{{ include "sfs-assessment-app.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/* Selector labels */}}
{{- define "sfs-assessment-app.selectorLabels" -}}
app.kubernetes.io/name: sfs-assessment-app
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/* Create chart name and version as used by the chart label. */}}
{{- define "sfs-assessment-app.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/* Create a default fully qualified app name. */}}
{{- define "sfs-assessment-app.fullname" -}}
{{- printf "%s-%s" .Release.Name "sfs-assessment-app" | trunc 63 | trimSuffix "-" }}
{{- end }}